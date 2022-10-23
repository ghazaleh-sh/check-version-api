package ir.sadad.co.checkversionapi.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * includes limitations of client operating system which makes an unhealthy os
 * @author g.shahrokhabadi
 */
@Getter
@Setter
@Entity
@Table(name = "BUSINESS_RULE", schema = "VERSION_API")
public class BusinessRule implements Serializable {

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "BS_TITLE", length = 50)
    private String bsTitle;

    @ManyToOne
    @JoinColumn(name = "VERSION_ID", referencedColumnName = "ID", nullable = false)
    @NotNull
    private Version version;

    @Column(name = "OS_CODE", nullable = false)
    @NotNull
    private Integer osCode;

    @Column(name = "OS_VERSION", nullable = false)
    @NotNull
    private Integer osVersion;
}
