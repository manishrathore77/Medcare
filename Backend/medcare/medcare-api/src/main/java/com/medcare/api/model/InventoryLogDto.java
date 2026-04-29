package com.medcare.api.model;


/**
 * Data transfer object for InventoryLog in API contracts.
 */

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InventoryLogDto {

    private Long id;
    private Long medicineId;
    private InventoryChangeType changeType;
    private Integer quantity;
    private String reason;
}
