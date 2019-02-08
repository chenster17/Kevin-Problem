package ca.utoronto.utm.mcs;

import org.neo4j.driver.v1.AuthTokens;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.GraphDatabase;
import org.neo4j.driver.v1.Record;
import org.neo4j.driver.v1.Session;
import org.neo4j.driver.v1.StatementResult;
import org.neo4j.driver.v1.Transaction;
import org.neo4j.driver.v1.TransactionWork;
import org.neo4j.driver.v1.exceptions.NoSuchRecordException;
import org.json.*;
import static org.neo4j.driver.v1.Values.parameters;
import java.util.*;
import java.util.List;

public class BaconQuery implements AutoCloseable
{
    private final Driver driver;

    public BaconQuery( String uri, String user, String password )
    {
        driver = GraphDatabase.driver( uri, AuthTokens.basic( user, password ) );
    }

    @Override
    public void close() throws Exception
    {
        driver.close();
    }

    public JSONObject getBaconNumber( final String actorId )
    {
    	JSONObject actorGet=new JSONObject();
        try ( Session session = driver.session() )
        {	
        	actorGet = session.writeTransaction( new TransactionWork<JSONObject>()
            {
                @Override
                public JSONObject execute( Transaction tx )
                {
                	StatementResult resultMovie =  tx.run(
                									"MATCH (a:actor {actorId:$aId})-[r:ACTED_IN]->(movies)"+
                    								" return movies.movieId;",
                            parameters( "aId", actorId) );
                	StatementResult resultActorId =  tx.run(
													"MATCH (a:actor {actorId:$aId}) return distinct a.actorId;" ,
							parameters( "aId", actorId) );
                	StatementResult resultActorName =  tx.run(
													"MATCH (a:actor {actorId:$aId}) return distinct a.name;" ,
							parameters( "aId", actorId) );
                                      

                	JSONObject aGJO = new JSONObject();
                	List <Record> myGAList = resultMovie.list();
                
					try {
						aGJO = new JSONObject();
						for(int i= 0; i<myGAList.size();i++) {
							aGJO.accumulate("movies", myGAList.get(i).get(0).asObject());
						}
						aGJO.accumulate("actorId",resultActorId.single().get(0).asObject());
						aGJO.accumulate("name",resultActorName.single().get(0).asObject());

						
					} catch (NoSuchRecordException e) {
						//e.printStackTrace();
						throw e;
					} catch (JSONException e) {
						e.printStackTrace();
					}

                    return aGJO;
                }
            } );

        } catch (NoSuchRecordException e) {
			//e.printStackTrace();
			throw e;
        }
		return actorGet;
    }}