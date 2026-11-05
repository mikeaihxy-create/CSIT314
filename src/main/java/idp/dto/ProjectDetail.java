package idp.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record ProjectDetail(String title, String description, String style, BigDecimal budget, long categoryID, LocalDate completionDate, List<String> imageUrls) {
}
