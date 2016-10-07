/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.ed.collaborate.service;

import java.util.List;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.filter.AndFilter;
import org.springframework.ldap.filter.EqualsFilter;
import org.springframework.ldap.filter.OrFilter;
import org.springframework.stereotype.Service;
import uk.ac.ed.collaborate.data.User;

/**
 * Service Class for retrieving LDAP user lookups
 * @author Richard Good
 */
@Service
public class LdapService {

    private final Log logger = LogFactory.getLog(this.getClass());

    @Autowired
    private LdapTemplate ldapTemplate;

    public class PersonAttributeMapper implements AttributesMapper{

        private final Log logger = LogFactory.getLog(this.getClass());

        /**
         * Maps the basic user attributes
         * @param attributes
         * @return
         * @throws NamingException
         */
        @Override
        public Object mapFromAttributes(Attributes attributes) throws NamingException {
            User user = new User();

            // Users may not have an email address
            if (attributes.get("mail")!=null)
            {
                String email = (String)attributes.get("mail").get();
                if (email!=null)
                {
                    logger.debug("Setting email:"+email);
                    user.setEmail(email);
                }

            }

            String uid = (String)attributes.get("uid").get();
            if (uid!=null)
            {
                logger.debug("Setting uid:"+uid);
                user.setUsernameInternal(uid);
            }

            String cn = (String)attributes.get("cn").get();
            if (cn!=null)
            {
                logger.debug("Setting cn:"+cn);
                user.setDisplayName(cn);
            }

            return user;
        }

    }

    /**
     * Gets a User from a passed in searchTerm. Checks for match on cn or uid
     * @param searchTerm
     * @return User
     */
    @SuppressWarnings("unchecked")
    public User getUserDetails(String searchTerm)
    {
        logger.debug("getUserDetails called");
        AndFilter andFilter = new AndFilter();
        andFilter.and(new EqualsFilter("objectclass","person"));
        OrFilter orFilter = new OrFilter();
        orFilter.or(new EqualsFilter("uid",searchTerm));
        orFilter.or(new EqualsFilter("cn",searchTerm));
        andFilter.and(orFilter);
        logger.debug("Set up the filter for searchTerm:"+searchTerm);
        List<User> result;
        result = ldapTemplate.search("",andFilter.encode(),new PersonAttributeMapper());
        logger.debug("gotten a result");
        if (result.size()>0)
        {
            logger.debug("returning first result");
            return result.get(0);
        }
        else
        {
            logger.debug("no-one found, returning null");
            return null;
        }
    }
}
