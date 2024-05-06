package towssome.server.entity;

import jakarta.persistence.*;

@Entity
public class HashTag {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "hashtag_id")
    private Long id;
    private String name;
    private Long count;
}
