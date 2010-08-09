package de.objectcode.time4u.server.ejb.seam.api.filter;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.el.ELContext;
import javax.el.ExpressionFactory;
import javax.persistence.Query;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import de.objectcode.time4u.server.api.data.EntityType;
import de.objectcode.time4u.server.ejb.seam.api.report.parameter.BaseParameterValue;
import de.objectcode.time4u.server.ejb.util.ReportEL;

@XmlType(name = "project")
@XmlRootElement(name = "project")
public class ProjectFilter implements IFilter
{
  private static final long serialVersionUID = 867092515665550531L;

  private String m_projectId;
  private String m_projectIdExpression;
  private List<String> m_mainProjectidWithChildIds;

  public ProjectFilter()
  {
  }

  public ProjectFilter(final String projectId)
  {
    m_projectId = projectId;
  }

  @XmlAttribute(name = "project-id")
  public String getProjectId()
  {
    return m_projectId;
  }

  public void setProjectId(final String projectId)
  {
    m_projectId = projectId;
  }

  @XmlAttribute(name = "project-id-expression")
  public String getProjectIdExpression()
  {
    return m_projectIdExpression;
  }

  public void setProjectIdExpression(final String projectIdExpression)
  {
    m_projectIdExpression = projectIdExpression;
  }


  public List<String> getMainProjectidWithChildIds() {
    return m_mainProjectidWithChildIds;
  }

  public void setMainProjectidWithChildIds(
      List<String> mainProjectidWithChildIds) {
    this.m_mainProjectidWithChildIds = mainProjectidWithChildIds;
  }

  /**
   * {@inheritDoc}
   */
  public String getWhereClause(final EntityType entityType, final Map<String, BaseParameterValue> parameters,
      final ELContext context)
  {
    switch (entityType) {
      case WORKITEM:
        return "(w.project.id in (:projectIds))";
      case TODO:
        return "(t.task.project.id in (:projectIds))";
      default:
        throw new RuntimeException("PersonFilter not applicable for entity type: " + entityType);
    }
  }

  /**
   * {@inheritDoc}
   */
  public void setQueryParameters(final EntityType entityType, final Query query,
      final Map<String, BaseParameterValue> parameters, final ELContext context)
  {
    if (m_projectIdExpression != null) {
      final ExpressionFactory factory = ReportEL.getExpressionFactory();

      query.setParameter("projectId", factory.createValueExpression(context, m_projectIdExpression, String.class)
          .getValue(context));
    } else {
      query.setParameter("projectIds", m_mainProjectidWithChildIds);
    }
  }

  public static ProjectFilter filterProject(final String projectId)
  {
    return new ProjectFilter(projectId);
  }
}
