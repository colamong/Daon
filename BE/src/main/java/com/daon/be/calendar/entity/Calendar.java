package com.daon.be.calendar.entity;

import java.time.LocalDate;

import com.daon.be.child.entity.ChildProfile;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "Calendar", uniqueConstraints = {
	@UniqueConstraint(columnNames = {"child_id", "date"})
})
public class Calendar {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "child_id", nullable = false)
	private ChildProfile child;

	@Column(nullable = false)
	private LocalDate date;

	@OneToOne(mappedBy = "calendar")
	private ImageDiary imageDiary;

	public static Calendar of(ChildProfile child, LocalDate date) {
		Calendar c = new Calendar();
		c.setChild(child);
		c.setDate(date);
		return c;
	}
}