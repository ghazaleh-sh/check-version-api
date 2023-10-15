package ir.sadad.co.checkversionapi.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * client application information
 * @author g.shahrokhabadi
 */
@Getter
@Setter
@Entity
//@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Table(name = "APPLICATION_INFO", schema = "VERSION_API")
public class ApplicationInfo implements Serializable {

    private static final long serialVersionUID = -8718640914475412488L;
    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "APP_NAME", length = 50, nullable = false)
    @NotNull
    private String appName;

    @Column(name = "APP_DESCRIPTION", length = 50)
    private String appDescription;

    @Column(name = "OS_CODE", length = 50, nullable = false)
    @NotNull
    private Integer osCode;
}
