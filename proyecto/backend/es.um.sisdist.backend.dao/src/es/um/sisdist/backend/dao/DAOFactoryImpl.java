/**
 *
 */
package es.um.sisdist.backend.dao;

import es.um.sisdist.backend.dao.database.IDatabaseDAO;
import es.um.sisdist.backend.dao.database.MongoDatabaseDAO;
import es.um.sisdist.backend.dao.database.SQLDatabaseDAO;
import es.um.sisdist.backend.dao.databaseMapReduce.IDatabaseMapReduce;
import es.um.sisdist.backend.dao.databaseMapReduce.MongoDatabaseMapReduceDAO;
import es.um.sisdist.backend.dao.user.IUserDAO;
import es.um.sisdist.backend.dao.user.MongoUserDAO;
import es.um.sisdist.backend.dao.user.SQLUserDAO;

/**
 *
 */
public class DAOFactoryImpl implements IDAOFactory
{
    @Override
    public IUserDAO createSQLUserDAO()
    {
        return new SQLUserDAO();
    }

    @Override
    public IUserDAO createMongoUserDAO()
    {
        return new MongoUserDAO();
    }
    
    @Override
	public IDatabaseDAO createSQLDatabaseDAO() 
    {
		return new SQLDatabaseDAO();
	}

	@Override
	public IDatabaseDAO createMongoDatabaseDAO() 
	{
		return new MongoDatabaseDAO();
	}

	@Override
	public IDatabaseMapReduce createSQLDatabaseMrDAO() {
		// TODO Auto-generated method stub
		return null; // no se usa la bd SQL
	}

	@Override
	public IDatabaseMapReduce createMongoDatabaseMrDAO() {
		// TODO Auto-generated method stub
		return new MongoDatabaseMapReduceDAO();
	}

}