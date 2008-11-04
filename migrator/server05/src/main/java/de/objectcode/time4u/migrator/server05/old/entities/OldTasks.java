package de.objectcode.time4u.migrator.server05.old.entities;
// Generated 06.10.2008 17:02:23 by Hibernate Tools 3.2.0.CR1


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.IDENTITY;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * OldTasks generated by hbm2java
 */
@Entity
@Table(name="TASKS"
)
public class OldTasks  implements java.io.Serializable {

  private static final long serialVersionUID = 1L;
  
     private Long id;
     private String name;
     private boolean active;
     private boolean deleted;
     private Long projectId;
     private String description;
     private boolean generic;
     private Long revision;

    public OldTasks() {
    }

	
    public OldTasks(String name, boolean active, boolean deleted, boolean generic) {
        this.name = name;
        this.active = active;
        this.deleted = deleted;
        this.generic = generic;
    }
    public OldTasks(String name, boolean active, boolean deleted, Long projectId, String description, boolean generic, Long revision) {
       this.name = name;
       this.active = active;
       this.deleted = deleted;
       this.projectId = projectId;
       this.description = description;
       this.generic = generic;
       this.revision = revision;
    }
   
     @Id @GeneratedValue(strategy=IDENTITY)
    
    @Column(name="id", unique=true, nullable=false)
    public Long getId() {
        return this.id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    @Column(name="name", nullable=false, length=30)
    public String getName() {
        return this.name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    @Column(name="active", nullable=false)
    public boolean isActive() {
        return this.active;
    }
    
    public void setActive(boolean active) {
        this.active = active;
    }
    
    @Column(name="deleted", nullable=false)
    public boolean isDeleted() {
        return this.deleted;
    }
    
    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }
    
    @Column(name="project_id")
    public Long getProjectId() {
        return this.projectId;
    }
    
    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }
    
    @Column(name="description", length=65535)
    public String getDescription() {
        return this.description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    @Column(name="generic", nullable=false)
    public boolean isGeneric() {
        return this.generic;
    }
    
    public void setGeneric(boolean generic) {
        this.generic = generic;
    }
    
    @Column(name="revision")
    public Long getRevision() {
        return this.revision;
    }
    
    public void setRevision(Long revision) {
        this.revision = revision;
    }




}

