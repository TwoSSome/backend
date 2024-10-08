package towssome.server.service;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import towssome.server.entity.Cluster;
import towssome.server.entity.Member;
import towssome.server.repository.ClusterRepository;
import towssome.server.repository.member.MemberRepository;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ClusterService {
    private final ClusterRepository clusterRepository;
    private final MemberRepository memberRepository;
    private final RestTemplate restTemplate = new RestTemplate();
    private final String pythonServiceUrl = "http://localhost:5000/userClustering";

    @Transactional
    public void performClustering() {
        Map<String, List<String>> clusteredUsers = fetchClusteredUsersFromPythonService();

        clusterRepository.deleteAllBy();

        for (Map.Entry<String, List<String>> entry : clusteredUsers.entrySet()) {
            String clusterNum = entry.getKey();
            List<String> userIds = entry.getValue();

            for (String userId : userIds) {
                Long id = Long.parseLong(userId);
                Member member = memberRepository.findById(id)
                        .orElseThrow(() -> new IllegalArgumentException("Invalid Member ID"));
                Cluster cluster = new Cluster(
                        Long.parseLong(clusterNum),
                        member
                );

                clusterRepository.save(cluster);
            }
        }
    }

    private Map<String, List<String>> fetchClusteredUsersFromPythonService() {
        try {
            ResponseEntity<Map<String, List<String>>> response = restTemplate.exchange(
                    pythonServiceUrl,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<Map<String, List<String>>>() {}
            );

            if (response.getStatusCode().is2xxSuccessful()) {
                return response.getBody();
            } else {
                throw new IllegalStateException("Failed to fetch clustering results from Python service.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error while fetching clustering results from Python service", e);
        }
    }
}
