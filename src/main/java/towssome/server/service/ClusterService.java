package towssome.server.service;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import towssome.server.dto.ListResultRes;
import towssome.server.entity.Cluster;
import towssome.server.entity.Member;
import towssome.server.repository.cluster.ClusterRepository;
import towssome.server.repository.member.MemberRepository;

import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class ClusterService {
    private final ClusterRepository clusterRepository;
    private final MemberRepository memberRepository;
    private final RestTemplate restTemplate = new RestTemplate();
    @Value("${flask.IP}")
    private String FLASK_IP;
    private final String pythonServiceUrl = FLASK_IP + "/userClustering";

    @Transactional
    public void performClustering(String accessToken) {
        Map<String, List<String>> clusteredUsers = fetchClusteredUsersFromPythonService(accessToken);

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

    private Map<String, List<String>> fetchClusteredUsersFromPythonService(String accessToken) {
        try {
            log.info(accessToken);
            HttpHeaders headers = new HttpHeaders();
            headers.set("access", accessToken);

            HttpEntity<Void> entity = new HttpEntity<>(headers);
            ResponseEntity<Map<String, List<String>>> response = restTemplate.exchange(
                    pythonServiceUrl,
                    HttpMethod.GET,
                    entity,
                    new ParameterizedTypeReference<>() {
                    }
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

    public List<Member> getClusterMembers(Long memberId) {
        return clusterRepository.findClustersByMemberId(memberId);
    }



}
