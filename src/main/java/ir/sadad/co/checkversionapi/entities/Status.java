package ir.sadad.co.checkversionapi.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Getter
@Setter
@Entity
@Table(name = "STATUS", schema = "VERSION_API")
public class Status implements Serializable {

    private static final long serialVersionUID = 8626033155338480271L;
    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "STATUS_CODE", nullable = false)
    @NotNull
    private Integer statusCode;

    @Column(name = "STATUS_TITLE", nullable = false)
    @NotNull
    private String statusTitle;

    @Column(name = "STATUS_DESCRIPTION")
    private String statusDescription;

}
