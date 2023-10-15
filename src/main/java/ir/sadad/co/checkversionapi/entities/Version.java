package ir.sadad.co.checkversionapi.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
 * stores different versions of per app
 * @author g.shahrokhabadi
 */
@Getter
@Setter
@Entity
//@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Table(name = "Version", schema = "VERSION_API", uniqueConstraints = {@UniqueConstraint(columnNames = {"VERSION_CODE", "APP_ID"}, name = "UKVERSION_VERSIONCODE_APPID")})
public class Version implements Serializable {

    private static final long serialVersionUID = -7408239447840978240L;
    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
//    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JoinColumn(name = "APP_ID", referencedColumnName = "ID", nullable = false)
    @NotNull
    private ApplicationInfo applicationInfo;

    @ManyToOne
//    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JoinColumn(name = "STATUS_ID", referencedColumnName = "ID", nullable = false)
    @NotNull
    private Status status;

    @Column(name = "VERSION_NAME", length = 50)
    private String versionName;

    @Column(name = "IS_NEW")
    private boolean enabled;

    @Column(name = "VERSION_CODE", length = 50)
    @NotNull
    private Integer versionCode;

    @Column(name = "VALIDITY_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date validityDate;

    @Column(name = "IS_SILENT", columnDefinition="SMALLINT default 0")
    private boolean silent;

}
