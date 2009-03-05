package de.objectcode.time4u.server.ejb.seam.impl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.jboss.annotation.ejb.LocalBinding;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.datamodel.DataModel;
import org.jboss.seam.annotations.security.Restrict;
import org.jboss.seam.security.Identity;

import de.objectcode.time4u.server.api.data.EntityType;
import de.objectcode.time4u.server.ejb.seam.api.IPersonServiceLocal;
import de.objectcode.time4u.server.entities.PersonEntity;
import de.objectcode.time4u.server.entities.TeamEntity;
import de.objectcode.time4u.server.entities.account.UserAccountEntity;
import de.objectcode.time4u.server.entities.revision.ILocalIdGenerator;
import de.objectcode.time4u.server.entities.revision.IRevisionGenerator;
import de.objectcode.time4u.server.entities.revision.IRevisionLock;

@Stateless
@Local(IPersonServiceLocal.class)
@LocalBinding(jndiBinding = "time4u-server/seam/PersonServiceSeam/local")
@Name("PersonService")
@AutoCreate
@Scope(ScopeType.CONVERSATION)
public class PersonServiceSeam implements IPersonServiceLocal
{
  @PersistenceContext(unitName = "time4u")
  private EntityManager m_manager;

  @EJB
  private IRevisionGenerator m_revisionGenerator;

  @EJB
  private ILocalIdGenerator m_idGenerator;

  @In("org.jboss.seam.security.identity")
  Identity m_identity;

  @DataModel("admin.personList")
  List<PersonEntity> m_persons;

  @DataModel("admin.activePersonList")
  List<PersonEntity> m_activePersons;

  @DataModel("user.allowedPersonList")
  List<PersonEntity> m_allowedPersons;

  @SuppressWarnings("unchecked")
  @Restrict("#{s:hasRole('admin')}")
  @Factory("admin.personList")
  public void initPersons()
  {
    final Query query = m_manager.createQuery("from " + PersonEntity.class.getName()
        + " p where p.deleted = false order by p.surname asc");

    m_persons = query.getResultList();
  }

  @SuppressWarnings("unchecked")
  @Restrict("#{s:hasRole('admin')}")
  @Factory("admin.activePersonList")
  public void initActivePersons()
  {
    final Query query = m_manager.createQuery("from " + PersonEntity.class.getName()
        + " p where p.deleted = false and (p.active is null or p.active = true) order by p.surname asc");

    m_activePersons = query.getResultList();
  }

  @Restrict("#{s:hasRole('admin')}")
  public PersonEntity getPerson(final String id)
  {
    return m_manager.find(PersonEntity.class, id);
  }

  @Restrict("#{s:hasRole('user')}")
  @Factory("user.allowedPersonList")
  public void initAllowedPersons()
  {
    final UserAccountEntity userAccount = m_manager.find(UserAccountEntity.class, m_identity.getPrincipal().getName());

    final Set<PersonEntity> allowedPersons = new TreeSet<PersonEntity>(new Comparator<PersonEntity>() {
      public int compare(final PersonEntity o1, final PersonEntity o2)
      {
        if (o1.getSurname().compareTo(o2.getSurname()) != 0) {
          return o1.getSurname().compareTo(o2.getSurname());
        }

        return o1.getId().compareTo(o2.getId());
      }
    });

    allowedPersons.add(userAccount.getPerson());
    for (final TeamEntity team : userAccount.getPerson().getResponsibleFor()) {
      for (final PersonEntity member : team.getMembers()) {
        if (member.getActive()) {
          allowedPersons.add(member);
        }
      }
    }

    m_allowedPersons = new ArrayList<PersonEntity>(allowedPersons);
  }

  @Restrict("#{s:hasRole('admin')}")
  public void storePerson(final PersonEntity personEntity)
  {
    final IRevisionLock revisionLock = m_revisionGenerator.getNextRevision(EntityType.PERSON, null);

    if (personEntity.getId() == null) {
      personEntity.setId(m_idGenerator.generateLocalId(EntityType.TEAM));
    }
    personEntity.setRevision(revisionLock.getLatestRevision());
    personEntity.setLastModifiedByClient(m_idGenerator.getClientId());
    m_manager.merge(personEntity);

    if (m_persons != null) {
      initPersons();
    }
    if (m_activePersons != null) {
      initActivePersons();
    }
  }

}