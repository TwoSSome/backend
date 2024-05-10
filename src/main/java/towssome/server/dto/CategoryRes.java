package towssome.server.dto;

import lombok.AllArgsConstructor;
import lombok.ToString;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public record CategoryRes(
        Long categoryId,
        String categoryName,
        //아이디만 넘겨주는 것이 좋은 방법인가??
        List<Long> reviewPostIds_InCategory
) {
}
