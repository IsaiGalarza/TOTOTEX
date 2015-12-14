/*
 * JBoss, Home of Professional Open Source
 * Copyright 2014, Red Hat, Inc. and/or its affiliates, and individual
 * contributors by the @authors tag. See the copyright.txt in the
 * distribution for a full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package bo.buffalo.data;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import bo.buffalo.model.Usuario;
import bo.buffalo.model.UsuarioRol;

import java.util.List;

@ApplicationScoped
public class UsuarioRolRepository {

	@Inject
	private EntityManager em;

	public UsuarioRol findById(int id) {
		return em.find(UsuarioRol.class, id);
	}



	@SuppressWarnings("unchecked")
	public List<UsuarioRol> findByEmail(String email) {
		try {

			System.out.println(">>>> Parametro: "+email);
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<UsuarioRol> criteria = cb.createQuery(UsuarioRol.class);
			CriteriaQuery<Usuario> criteriaUser = cb.createQuery(Usuario.class);
			Root<UsuarioRol> userRole = criteria.from(UsuarioRol.class);
			Root<Usuario> user = criteriaUser.from(Usuario.class);

			// Swap criteria statements if you would like to try out type-safe criteria queries, a new
			// feature in JPA 2.0
			// criteria.select(member).where(cb.equal(member.get(Member_.email), email));
			//criteria.select(userRole).where(cb.equal(userRole.get("user.email"), email));
			//criteriaUser.select(user).where(cb.equal(user.get("email"), email));
			//criteria.select(userRole).where(cb.equal(user.get("email"), email));

			//criteria.select(userRole).select(user).where(cb.equal(userRole.get("userr.email"), "isai.galarza@gmail.com"));
			//return em.createQuery(criteria).getResultList();

			String query = "select ur from UsuarioRol ur where ur.usuario.email='"+email+"'";
			return em.createQuery(query).getResultList();
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Error in findByEmail: "+e.getMessage());
			e.printStackTrace();
			return null;
		}
	}

	public UsuarioRol findByUsuario(Usuario usuario){
		try{
			String query = "select ur from UsuarioRol ur where ur.usuario.id="+usuario.getId();
			return (UsuarioRol) em.createQuery(query).getSingleResult();
		}catch(Exception e){
			return null;
		}
	}

	public List<UsuarioRol> findAllOrderedByName() {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<UsuarioRol> criteria = cb.createQuery(UsuarioRol.class);
		Root<UsuarioRol> userRole = criteria.from(UsuarioRol.class);
		// Swap criteria statements if you would like to try out type-safe criteria queries, a new
		// feature in JPA 2.0
		// criteria.select(member).orderBy(cb.asc(member.get(Member_.name)));
		criteria.select(userRole).orderBy(cb.asc(userRole.get("roles.name")));
		return em.createQuery(criteria).getResultList();
	}
}
