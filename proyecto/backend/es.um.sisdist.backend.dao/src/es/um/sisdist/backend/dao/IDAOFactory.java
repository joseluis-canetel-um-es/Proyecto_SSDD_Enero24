/**
 *
 */
package es.um.sisdist.backend.dao;

import es.um.sisdist.backend.dao.database.IDatabaseDAO;
import es.um.sisdist.backend.dao.databaseMapReduce.IDatabaseMapReduce;
import es.um.sisdist.backend.dao.user.IUserDAO;

/**
 *
 */
public interface IDAOFactory
{
    public IUserDAO createSQLUserDAO();

    public IUserDAO createMongoUserDAO();
    
    public IDatabaseDAO createSQLDatabaseDAO();

    public IDatabaseDAO createMongoDatabaseDAO();
    
    public IDatabaseMapReduce createSQLDatabaseMrDAO();

    public IDatabaseMapReduce createMongoDatabaseMrDAO();
    
    
}