package domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class Item {

    private Long id;

    private String title;

    private String data;

    private LocalDateTime updatedDate;

    private LocalDateTime createdDate;
}
