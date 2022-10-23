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
@Table(name = "SUB_FEATURE", schema = "VERSION_API")
public class SubFeature implements Serializable {

    private static final long serialVersionUID = 4155881403678821392L;
    @Id
    @Column(name = "ID")
    private Long id;

    @Column(name = "IS_NEW", nullable = false)
    private boolean newSubFeature;

    @ManyToOne
    @JoinColumn(name = "FEATURE_ID", referencedColumnName = "ID", nullable = false)
    @NotNull
    private Feature feature;
}
