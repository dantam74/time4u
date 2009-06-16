package de.objectcode.time4u.migrator.server05.old.entities;
// Generated 06.10.2008 17:02:23 by Hibernate Tools 3.2.0.CR1


import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.IDENTITY;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * OldProjectProperties generated by hbm2java
 */
@Entity
@Table(name="PROJECT_PROPERTIES"
)
public class OldProjectProperties  implements java.io.Serializable {

  private static final long serialVersionUID = 1L;
  
     private Long id;
     private String name;
     private Boolean boolValue;
     private String strValue;
     private Date dateValue;
     private Integer intValue;
     private long projectId;

    public OldProjectProperties() {
    }

	
    public OldProjectProperties(String name, long projectId) {
        this.name = name;
        this.projectId = projectId;
    }
    public OldProjectProperties(String name, Boolean boolValue, String strValue, Date dateValue, Integer intValue, long projectId) {
       this.name = name;
       this.boolValue = boolValue;
       this.strValue = strValue;
       this.dateValue = dateValue;
       this.intValue = intValue;
       this.projectId = projectId;
    }
   
     @Id @GeneratedValue(strategy=IDENTITY)
    
    @Column(name="id", unique=true, nullable=false)
    public Long getId() {
        return this.id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    @Column(name="name", nullable=false, length=200)
    public String getName() {
        return this.name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    @Column(name="boolValue")
    public Boolean getBoolValue() {
        return this.boolValue;
    }
    
    public void setBoolValue(Boolean boolValue) {
        this.boolValue = boolValue;
    }
    
    @Column(name="strValue", length=65535)
    public String getStrValue() {
        return this.strValue;
    }
    
    public void setStrValue(String strValue) {
        this.strValue = strValue;
    }
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="dateValue", length=19)
    public Date getDateValue() {
        return this.dateValue;
    }
    
    public void setDateValue(Date dateValue) {
        this.dateValue = dateValue;
    }
    
    @Column(name="intValue")
    public Integer getIntValue() {
        return this.intValue;
    }
    
    public void setIntValue(Integer intValue) {
        this.intValue = intValue;
    }
    
    @Column(name="project_id", nullable=false)
    public long getProjectId() {
        return this.projectId;
    }
    
    public void setProjectId(long projectId) {
        this.projectId = projectId;
    }




}


