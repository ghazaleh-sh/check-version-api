package ir.sadad.co.checkversionapi.entities;

import com.fasterxml.jackson.annotation.JsonInclude;
import ir.sadad.co.checkversionapi.enums.FeatureType;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.List;
import java.util.Objects;

/**
 * new features of applications
 * @author g.shahrokhabadi
 */
@Getter
@Setter
@Entity
@Table(name = "FEATURE", schema = "VERSION_API")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Feature implements Serializable {

    private static final long serialVersionUID = 5391539722433933968L;
    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "FEATURE_TITLE", length = 50, nullable = false)
    @NotNull
    private String featureTitle;

    @Column(name = "FEATURE_TYPE", nullable = false, columnDefinition = "CHAR(15) NOT NULL")
    @Enumerated(EnumType.STRING)
    @NotNull
    private FeatureType featureType;

    @Column(name = "IS_ENABLE")
    private boolean featureEnable;

    @Column(name = "FEATURE_CODE")
    private BigInteger featureCode;

    @Column(name = "FEATURE_DESCRIPTION", length = 50)
    private String featureDescription;

    @Transient
    private List<Feature> subFeatures;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Feature feature = (Feature) o;
        return id.equals(feature.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
