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

    public JSONObject getActor( final String actorId )
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

						
					} catch (JSONException e) {
						e.printStackTrace();
					} catch(Exception e){
						throw e;
					}

                    return aGJO;
                }
            } );

        } catch (Exception e){
        	throw e;
        }
		return actorGet;
    }
    public JSONObject getMovie( final String movieId )
    {
    	JSONObject movieGet=new JSONObject();
        try ( Session session = driver.session() )
        {
        	movieGet = session.writeTransaction( new TransactionWork<JSONObject>()
            {
                @Override
                public JSONObject execute( Transaction tx )
                {
                	StatementResult resultActor =  tx.run(
                									"MATCH (m:movie {movieId:$mId})<-[r:ACTED_IN]-(actors)"+
                    								" return actors.actorId;",
                            parameters( "mId", movieId) );
                	StatementResult resultMovieId =  tx.run(
													"MATCH (m:movie {movieId:$mId}) return distinct m.movieId;" ,
							parameters( "mId", movieId) );
                	StatementResult resultMovieName =  tx.run(
													"MATCH (m:movie {movieId:$mId}) return distinct m.name;" ,
							parameters( "mId", movieId) );
                                      

                	JSONObject mGJO = new JSONObject();
                	List <Record> myGMList = resultActor.list();

					try {
						mGJO = new JSONObject();
						for(int i= 0; i<myGMList.size();i++) {
							mGJO.accumulate("actors", myGMList.get(i).get(0).asObject());
						}
						mGJO.accumulate("movieId",resultMovieId.single().get(0).asObject());
						mGJO.accumulate("name",resultMovieName.single().get(0).asObject());

						
					}  catch (JSONException e) {
						e.printStackTrace();
					} catch (Exception e){
			        	throw e;
			        }

                    return mGJO;
                }
            } );

        } catch(Exception e){
        	//e.printStackTrace();
        	throw e;
        }
		return movieGet;
    }

	public void putActor(String actorId, String actorN) {
        try ( Session session = driver.session() )
        {
        	session.writeTransaction( new TransactionWork<JSONObject>()
            {
                @Override
                public JSONObject execute( Transaction tx )
                {
                	tx.run("MERGE (a:actor {actorId:$aId, name: $aName})",
                			parameters("aId",actorId, "aName",actorN));
                	JSONObject not = new JSONObject();
					return not;
                

                }
            } );

        } catch (Exception e){
        	throw e;
        }
		
}
	public void putMovie(String movieId, String movieN) {
        try ( Session session = driver.session() )
        {
        	session.writeTransaction( new TransactionWork<JSONObject>()
            {
                @Override
                public JSONObject execute( Transaction tx )
                {
                	tx.run("MERGE (m:movie {movieId:$mId, name: $mName})",
                			parameters("mId",movieId, "mName",movieN));
                	JSONObject not = new JSONObject();
					return not;
                

                }
            } );

        } catch (Exception e){
        	throw e;
        }
		
}
	public JSONObject hasRelation(String actorId, String movieId) {

    	JSONObject relationGet=new JSONObject();
        try ( Session session = driver.session() )
        {
        	relationGet = session.writeTransaction( new TransactionWork<JSONObject>()
            {
                @Override
                public JSONObject execute( Transaction tx )
                {
           
                	StatementResult resultRelationship =  tx.run(
                									"match (a:actor{actorId:$aId}),(m:movie{movieId:$mId}) return exists ((a)-[:ACTED_IN]->(m));",
                            parameters( "aId",actorId,"mId", movieId) );
                                      
                	JSONObject rGJO = new JSONObject();

  
					try {
						rGJO = new JSONObject();
						rGJO.accumulate("actorId",actorId);
						rGJO.accumulate("movieId",movieId);
						rGJO.accumulate("hasRelationship",resultRelationship.single().get(0).asObject());


						
					} catch (JSONException e) {
						e.printStackTrace();
					} catch (Exception e){
			        	throw e;
			        }

                    return rGJO;
                }
            } );

        } catch(Exception e){
        	//e.printStackTrace();
        	throw e;
        }
		return relationGet;
	}

	public void putRelation(String actorId, String movieId) {
        try ( Session session = driver.session() )
        {
        	session.writeTransaction( new TransactionWork<JSONObject>()
            {
                @Override
                public JSONObject execute( Transaction tx )
                {
                	tx.run("match (a:actor{actorId:$aId}),(m:movie{movieId:$mId})"+
                			"merge ((a)-[:ACTED_IN]->(m));",
                			parameters("aId",actorId, "mId",movieId));
                	JSONObject not = new JSONObject();
					return not;
                

                }
            } );

        } catch (Exception e){
        	throw e;
        }
		
}

	
}
