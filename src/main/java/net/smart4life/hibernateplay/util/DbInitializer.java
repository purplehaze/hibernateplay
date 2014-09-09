package net.smart4life.hibernateplay.util;

import net.smart4life.hibernateplay.model.*;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.Stateless;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.util.logging.Logger;

@Startup
@Singleton
public class DbInitializer {

//	private Server h2Server;
	
	@Inject
	private EntityManager em;
	
    @Inject
    private Logger log;
	
	@PostConstruct
	private void init(){
		log.info("!!!!!!!!!!!!!!!!!!!!!!!!!!! init DB");
//		startH2Server();
		
		initUserPerson();
		initOneToOneTwoSideRef();
		initOneToOneOneSideRef();
		initOneToOneManyToOne();
	}

	private void initUserPerson(){
		Company company = new Company();
		company.setId(1L);
		company.setName("Smart4Life");
		em.persist(company);
		em.flush();

		User u = new User();
		u.setId(1L);
		u.setUsername("aaa");
		u.setPassword("aaa");

		Person p = new Person();
		p.setId(1L);
		p.setFirstname("Roman");
		p.setSecondname("Ilin");
		u.setPerson(p);
		company.addPerson(p);
		em.persist(u);



		u = new User();
		u.setId(2L);
		u.setUsername("bbb");
		u.setPassword("bbb");

		p = new Person();
		p.setId(2L);
		p.setFirstname("Billy");
		p.setSecondname("Gates");
		p.setUser(u);
		company.addPerson(p);
		em.persist(u);


		em.persist(company);
	}

	private void initOneToOneTwoSideRef(){
		OneToOneTwoSideRefA a1 = new OneToOneTwoSideRefA();
		a1.setName("A1");
		OneToOneTwoSideRefB b1 = new OneToOneTwoSideRefB();
		b1.setName("B1");
		a1.setB(b1);
		em.persist(a1);

		OneToOneTwoSideRefA a2 = new OneToOneTwoSideRefA();
		a2.setName("A2");
		OneToOneTwoSideRefB b2 = new OneToOneTwoSideRefB();
		b2.setName("B2");
		a2.setB(b2);
		em.persist(a2);
	}

	private void initOneToOneOneSideRef(){
		OneToOneOneSideRefA a1 = new OneToOneOneSideRefA();
		a1.setName("A1");
		OneToOneOneSideRefB b1 = new OneToOneOneSideRefB();
		b1.setName("B1");
		a1.setB(b1);
		em.persist(a1);

		OneToOneOneSideRefA a2 = new OneToOneOneSideRefA();
		a2.setName("A2");
		OneToOneOneSideRefB b2 = new OneToOneOneSideRefB();
		b2.setName("B2");
		a2.setB(b2);
		em.persist(a2);
	}

	private void initOneToOneManyToOne(){
		OneToOneManyToOneA a1 = new OneToOneManyToOneA();
		a1.setName("A1");
		OneToOneManyToOneB b1 = new OneToOneManyToOneB();
		b1.setName("B1");

		a1.setB(b1);
		b1.setA(a1);

		em.persist(a1);

		OneToOneManyToOneA a2 = new OneToOneManyToOneA();
		a2.setName("A2");
		OneToOneManyToOneB b2 = new OneToOneManyToOneB();
		b2.setName("B2");

		a2.setB(b2);
		b2.setA(a2);

		em.persist(a2);
	}

//	private void startH2Server(){
//		try {
//			h2Server = Server.createTcpServer("-tcp", "-tcpAllowOthers").start();
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}
//	}

}
