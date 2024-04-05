package towssome.server.entity;

import jakarta.persistence.*;

@Entity
public class HashTag {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "hashtag_id")
    Long id;
    String name;
    Long count;
}
