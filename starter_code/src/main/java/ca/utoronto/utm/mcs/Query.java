package ca.utoronto.utm.mcs;

import org.neo4j.driver.v1.AuthTokens;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.GraphDatabase;
import org.neo4j.driver.v1.Session;
import org.neo4j.driver.v1.StatementResult;
import org.neo4j.driver.v1.Transaction;
import org.neo4j.driver.v1.TransactionWork;

import static org.neo4j.driver.v1.Values.parameters;

public class Query implements AutoCloseable
{
    private final Driver driver;

    public Query( String uri, String user, String password )
    {
        driver = GraphDatabase.driver( uri, AuthTokens.basic( user, password ) );
    }

    @Override
    public void close() throws Exception
    {
        driver.close();
    }

    public String getActor( final String actorId )
    {
    	String actor="";
        try ( Session session = driver.session() )
        {
            actor = session.writeTransaction( new TransactionWork<String>()
            {
                @Override
                public String execute( Transaction tx )
                {
                    StatementResult result = tx.run( "match (a:actor{id:$aId})" +
                    								"return  distinct a { .* }",
                            parameters( "aId", actorId) );
                    return result.;
                }
            } );

        }
		return actor;
    }
}
