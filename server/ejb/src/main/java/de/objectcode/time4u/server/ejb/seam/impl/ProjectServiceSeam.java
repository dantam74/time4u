package de.objectcode.time4u.server.ejb.seam.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.security.Restrict;

import de.objectcode.time4u.server.api.data.EntityType;
import de.objectcode.time4u.server.ejb.seam.api.IProjectServiceLocal;
import de.objectcode.time4u.server.entities.DayInfoEntity;
import de.objectcode.time4u.server.entities.PersonEntity;
import de.objectcode.time4u.server.entities.ProjectEntity;
import de.objectcode.time4u.server.entities.TaskEntity;
import de.objectcode.time4u.server.entities.WorkItemEntity;
import de.objectcode.time4u.server.entities.revision.ILocalIdGenerator;
import de.objectcode.time4u.server.entities.revision.IRevisionGenerator;
import de.objectcode.time4u.server.entities.revision.IRevisionLock;

@Stateless
@Local(IProjectServiceLocal.class)
@org.jboss.annotation.ejb.LocalBinding(jndiBinding = "time4u-server/seam/ProjectServiceSeam/local")
@org.jboss.ejb3.annotation.LocalBinding(jndiBinding = "time4u-server/seam/ProjectServiceSeam/local")
@Name("ProjectService")
@AutoCreate
@Scope(ScopeType.CONVERSATION)
public class ProjectServiceSeam implements IProjectServiceLocal
{
  @PersistenceContext(unitName = "time4u")
  private EntityManager m_manager;

  @EJB
  private IRevisionGenerator m_revisionGenerator;

  @EJB
  private ILocalIdGenerator m_idGenerator;

  @Restrict("#{s:hasRole('user')}")
  public ProjectEntity getProject(final String projectId)
  {
    return m_manager.find(ProjectEntity.class, projectId);
  }

  @SuppressWarnings("unchecked")
  @Restrict("#{s:hasRole('user')}")
  public List<ProjectEntity> getRootProjects(final boolean deleted, final boolean onlyActive)
  {
    final StringBuffer queryString = new StringBuffer("from " + ProjectEntity.class.getName()
        + " p where p.parent is null");

    if (!deleted) {
      queryString.append(" and p.deleted = false");
    }
    if (onlyActive) {
      queryString.append(" and p.active = true");
    }
    queryString.append(" order by p.name");

    final Query query = m_manager.createQuery(queryString.toString());

    return query.getResultList();
  }

  @SuppressWarnings("unchecked")
  @Restrict("#{s:hasRole('user')}")
  public List<ProjectEntity> getChildProjects(final String projectId, final boolean deleted, final boolean onlyActive)
  {
    if (projectId == null || projectId.length() == 0) {
      return getRootProjects(deleted, onlyActive);
    }
    final StringBuffer queryString = new StringBuffer("from " + ProjectEntity.class.getName()
        + " p where p.parent.id = :projectId");

    if (!deleted) {
      queryString.append(" and p.deleted = false");
    }
    if (onlyActive) {
      queryString.append(" and p.active = true");
    }
    queryString.append(" order by p.name");

    final Query query = m_manager.createQuery(queryString.toString());

    query.setParameter("projectId", projectId);

    return query.getResultList();
  }

  @SuppressWarnings("unchecked")
  @Restrict("#{s:hasRole('user')}")
  public List<TaskEntity> getTasks(final String projectId)
  {
    final Query query = m_manager.createQuery("from " + TaskEntity.class.getName()
        + " t where t.project.id = :projectId and t.deleted = false order by t.name");

    query.setParameter("projectId", projectId);

    return query.getResultList();
  }

  @SuppressWarnings("unchecked")
  @Restrict("#{s:hasRole('admin')}")
  public Map<PersonEntity, Long> checkTransferData(final String fromTaskId)
  {
    final Query query = m_manager.createQuery("select w.dayInfo.person, count(*) from "
        + WorkItemEntity.class.getName() + " w where w.task.id = :taskId group by w.dayInfo.person.id");

    query.setParameter("taskId", fromTaskId);

    final List<Object[]> rows = query.getResultList();
    final Map<PersonEntity, Long> result = new HashMap<PersonEntity, Long>();

    for (final Object[] row : rows) {
      result.put((PersonEntity) row[0], (Long) row[1]);
    }

    return result;
  }

  @SuppressWarnings("unchecked")
  @Restrict("#{s:hasRole('admin')}")
  public void transferData(final List<String> personIds, final String fromTaskId, final String toTaskId)
  {
    final TaskEntity fromTask = m_manager.find(TaskEntity.class, fromTaskId);
    final TaskEntity toTask = m_manager.find(TaskEntity.class, toTaskId);
    final ProjectEntity toProject = toTask.getProject();

    for (final String personId : personIds) {
      final PersonEntity person = m_manager.find(PersonEntity.class, personId);
      final IRevisionLock revisionLock = m_revisionGenerator.getNextRevision(EntityType.DAYINFO, person.getId());

      final Query query = m_manager.createQuery("from " + WorkItemEntity.class.getName()
          + " w join fetch w.dayInfo where w.task.id = :taskId and w.dayInfo.person.id = :personId");

      query.setParameter("taskId", fromTask.getId());
      query.setParameter("personId", person.getId());

      final List<WorkItemEntity> result = query.getResultList();

      for (final WorkItemEntity workItem : result) {
        final DayInfoEntity dayInfo = workItem.getDayInfo();

        dayInfo.setLastModifiedByClient(m_idGenerator.getClientId());
        dayInfo.setRevision(revisionLock.getLatestRevision());
        workItem.setProject(toProject);
        workItem.setTask(toTask);
      }

      m_manager.flush();
      m_manager.clear();
    }
  }

  @Restrict("#{s:hasRole('admin')}")
  public void deleteProject(final String projectId)
  {
    final ProjectEntity project = m_manager.find(ProjectEntity.class, projectId);

    if (project != null) {
      doDeleteProject(project);

      m_manager.flush();
      m_manager.clear();
    }

  }

  @SuppressWarnings("unchecked")
  private void doDeleteProject(final ProjectEntity project)
  {
    final Query childProjectQuery = m_manager.createQuery("from " + ProjectEntity.class.getName()
        + " p where p.parent = :parent and p.deleted = false");
    childProjectQuery.setParameter("parent", project);

    final List<ProjectEntity> childProjects = childProjectQuery.getResultList();

    // First delete all not yet deleted child projects
    for (final ProjectEntity childProject : childProjects) {
      doDeleteProject(childProject);
    }

    final Query taskQuery = m_manager.createQuery("from " + TaskEntity.class.getName()
        + " t where t.project = :project and t.deleted = false");
    taskQuery.setParameter("project", project);

    final List<TaskEntity> tasks = taskQuery.getResultList();

    // Delete all not yet deleted tasks of the project
    for (final TaskEntity task : tasks) {
      final IRevisionLock revisionLock = m_revisionGenerator.getNextRevision(EntityType.TASK, null);

      task.setLastModifiedByClient(m_idGenerator.getClientId());
      task.setRevision(revisionLock.getLatestRevision());
      task.setDeleted(true);
    }

    final IRevisionLock revisionLock = m_revisionGenerator.getNextRevision(EntityType.PROJECT, null);

    project.setLastModifiedByClient(m_idGenerator.getClientId());
    project.setRevision(revisionLock.getLatestRevision());
    project.setDeleted(true);

  }

  @Restrict("#{s:hasRole('admin')}")
  public void undeleteProject(final String projectId)
  {
    final ProjectEntity project = m_manager.find(ProjectEntity.class, projectId);

    if (project != null) {
      final IRevisionLock revisionLock = m_revisionGenerator.getNextRevision(EntityType.PROJECT, null);

      project.setLastModifiedByClient(m_idGenerator.getClientId());
      project.setRevision(revisionLock.getLatestRevision());
      project.setDeleted(false);

      m_manager.flush();
      m_manager.clear();
    }
  }

}
