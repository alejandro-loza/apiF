package mx.finerio.api.domain

import javax.persistence.*
import javax.validation.constraints.*

import groovy.transform.ToString
import org.hibernate.annotations.GenericGenerator

@Entity
@Table(name = 'concept')
@ToString(includes = 'id', includeNames = true, includePackage = false)
public class Concept{

    enum Type {
        DEFAULT,
        USER
    }


    @Id
    @Column(name = 'id', nullable = false, updatable = false)
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    String id

    @Column(name = 'description', nullable = false)
    String description

    @Column(name = 'amount', nullable = false)
    BigDecimal amount

    @Enumerated(EnumType.STRING)
    @Column(name = 'type', nullable = false)
    Type type

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = 'category_id', nullable = false)
    Category category
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = 'movement_id', nullable = false)
    Movement movement
    
    @Column(name = 'version', nullable = false)
    Long version

}
