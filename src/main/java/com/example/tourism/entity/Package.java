package com.example.tourism.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.UniqueElements;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "package",uniqueConstraints = {
        @UniqueConstraint(columnNames = {"package_name","description"})
})
public class Package {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "package_name")
    private String packageName;
    @Column(name = "description")
    private String description;
    @Column(name = "group_size")
    private Integer groupSize;
    @Column(name = "duration")
    private Integer duration;
    @Column(name = "places")
    private Integer places;
    @Column(name = "transportation")
    private String transportation;
    @Column(name = "price")
    private Double price;
    @Column(name = "category_id")
    private Long categoryId;
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    public Package(String packageName, String description, Integer groupSize, Integer duration, Integer places, String transportation, Double price, Long categoryId) {
        this.packageName = packageName;
        this.description = description;
        this.groupSize = groupSize;
        this.duration = duration;
        this.places = places;
        this.transportation = transportation;
        this.price = price;
        this.categoryId = categoryId;
    }
}
