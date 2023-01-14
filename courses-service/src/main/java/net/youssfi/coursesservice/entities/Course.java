package net.youssfi.coursesservice.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.util.List;

@Entity
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Course {
    @Id
    private String id;
    private String title;
    private String groupName;
    private String fileName;
    private String pictureURL;
    @ManyToOne
    private Teacher teacher;
    @OneToMany(mappedBy = "course")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private List<Subscription> subscriptions;

}
