package de.objectcode.time4u.server.api.data;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Person DTO object.
 * 
 * @author junglas
 */
@XmlType(name = "person")
@XmlRootElement(name = "person")
public class Person extends PersonSummary
{
  private static final long serialVersionUID = -4022407515199667835L;

  /** Map of all meta properties of the person. */
  private Map<String, MetaProperty> m_metaProperties;

  public Map<String, MetaProperty> getMetaProperties()
  {
    if (m_metaProperties == null) {
      return Collections.emptyMap();
    }
    return m_metaProperties;
  }

  public void setMetaProperties(final Map<String, MetaProperty> metaProperties)
  {
    m_metaProperties = metaProperties;
  }

  public void setMetaProperty(final MetaProperty metaProperty)
  {
    if (m_metaProperties == null) {
      m_metaProperties = new HashMap<String, MetaProperty>();
    }
    m_metaProperties.put(metaProperty.getName(), metaProperty);
  }

  public MetaProperty getMetaProperty(final String name)
  {
    if (m_metaProperties != null) {
      return m_metaProperties.get(name);
    }
    return null;
  }

}
