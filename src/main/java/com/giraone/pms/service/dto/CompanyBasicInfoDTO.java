package com.giraone.pms.service.dto;

import io.swagger.annotations.ApiModel;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for a company's basic information entity.
 */
@ApiModel(description = "The Company basic information.")
public class CompanyBasicInfoDTO implements Serializable {

    private Long id;

    @NotNull
    private String externalId;

    private String name;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        CompanyBasicInfoDTO companyDTO = (CompanyBasicInfoDTO) o;
        if (companyDTO.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), companyDTO.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "CompanyBasicInfoDTO{" +
            "id=" + id +
            ", externalId='" + externalId + '\'' +
            ", name='" + name + '\'' +
            '}';
    }
}
