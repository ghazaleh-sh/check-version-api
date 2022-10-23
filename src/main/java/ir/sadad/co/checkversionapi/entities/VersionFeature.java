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
@Table(name = "VERSION_FEATURE", schema = "VERSION_API")
public class VersionFeature implements Serializable {

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "VERSION_ID", referencedColumnName = "ID", nullable = false)
    @NotNull
    private Version version;

    @ManyToOne
    @JoinColumn(name = "FEATURE_ID", referencedColumnName = "ID", nullable = false)
    @NotNull
    private Feature feature;
}
