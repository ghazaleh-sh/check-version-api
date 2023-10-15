package ir.sadad.co.checkversionapi.entities;


import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Getter
@Setter
@Entity
//@Cacheable
//@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Table(name = "FLAVOR", schema = "VERSION_API")
public class Flavor implements Serializable {

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "FLAVOR_NAME", length = 200)
    @NotNull
    private String flavorName;

    @Column(name = "DOWNLOAD_LINK", nullable = false)
    @NotNull
    private String downloadLink;

    @ManyToOne
//    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JoinColumn(name = "APP_ID", referencedColumnName = "ID", nullable = false)
    @NotNull
    private ApplicationInfo applicationInfo;
}
