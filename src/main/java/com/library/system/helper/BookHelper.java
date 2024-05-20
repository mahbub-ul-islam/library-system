package com.library.system.helper;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Created by mahbub.islam on 19/5/2024.
 */
@Component
public class BookHelper {

    public void pagination(int page, Page<?> lists, Map<Object, Object> responseMap) {
        int totalPages = lists.getTotalPages();
        responseMap.put("lists", lists);
        responseMap.put("totalPages", totalPages);
        responseMap.put("currentPage", page);
    }
}
