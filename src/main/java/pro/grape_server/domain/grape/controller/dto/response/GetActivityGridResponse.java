package pro.grape_server.domain.grape.controller.dto.response;

import java.util.List;

public record GetActivityGridResponse(List<List<Integer>> weeks) {
}
