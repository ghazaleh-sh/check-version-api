package ir.sadad.co.checkversionapi.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * stores features of each version
 * @author g.shahrokhabadi
 */
@Getter
@Setter
@Entity
//@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Table(name = "VERSION_FEATURE", schema = "VERSION_API")
public class VersionFeature implements Serializable {

    private static final long serialVersionUID = -8024914451035866649L;
    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
//    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JoinColumn(name = "VERSION_ID", referencedColumnName = "ID", nullable = false)
    @NotNull
    private Version version;

    @ManyToOne
//    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JoinColumn(name = "FEATURE_ID", referencedColumnName = "ID", nullable = false)
    @NotNull
    private Feature feature;
}
