package ir.sadad.co.checkversionapi.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * includes features of an parent feature
 * @author g.shahrokhabadi
 */
@Getter
@Setter
@Entity
//@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Table(name = "SUB_FEATURE", schema = "VERSION_API")
public class SubFeature implements Serializable {

    private static final long serialVersionUID = 4155881403678821392L;

    @Id
    @Column(name = "AUTO_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long autoId;

    @Column(name = "ID")
    private Long id;

    @Column(name = "IS_NEW", nullable = false)
    private boolean newSubFeature;

    @ManyToOne
//    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JoinColumn(name = "FEATURE_ID", referencedColumnName = "ID", nullable = false)
    @NotNull
    private Feature feature;
}
