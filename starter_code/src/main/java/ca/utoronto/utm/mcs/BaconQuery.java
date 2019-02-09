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
    	JSONObject baconNum=new JSONObject();
    	if(actorId.equals("nm0000102")) {
    		try {
    			baconNum.accumulate("baconNumber","0");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    		return baconNum;
    	}
        try ( Session session = driver.session() )
        {	
        	baconNum = session.writeTransaction( new TransactionWork<JSONObject>()
            {
                @Override
                public JSONObject execute( Transaction tx )
                {
                	StatementResult resultBaconNumber =  tx.run(
                									"MATCH p=shortestPath((a:actor {id:\"nm0000102\"})-[*]-(b:actor {id:$aId}))" + 
                									"return length([h in nodes(p) WHERE h:actor])",
                            parameters( "aId", actorId) );

                                     

                	JSONObject bNJO = new JSONObject();
                	int bNum =resultBaconNumber.single().get(0).asInt()-1;
					try {
						bNJO.accumulate("baconNumber",Integer.toString(bNum));

						
					} catch (NoSuchRecordException e) {
						e.printStackTrace();
						throw e;
					} catch (JSONException e) {
						e.printStackTrace();
					}

                    return bNJO;
                }
            } );

        } catch (NoSuchRecordException e) {
			//e.printStackTrace();
			throw e;
        }
		return baconNum;
   }
    public JSONObject getBaconPath( final String actorId )
    {
    	JSONObject baconPath=new JSONObject();
    	if(actorId.equals("nm0000102")) {
    		List <JSONObject> myBPath = new ArrayList <JSONObject>();
    		try {
				baconPath.accumulate("baconNumber",0);
				baconPath.accumulate("baconPath",myBPath);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    		return baconPath;
    	}
        try ( Session session = driver.session() )
        {	
        	baconPath = session.writeTransaction( new TransactionWork<JSONObject>()
            {
                @Override
                public JSONObject execute( Transaction tx )
                {
                	StatementResult resultBaconNumber =  tx.run(
													"MATCH p=shortestPath((a:actor {id:\"nm0000102\"})-[*]-(b:actor {id:$aId}))" + 
													"return length([h in nodes(p) WHERE h:actor])",
							parameters( "aId", actorId) );
                	StatementResult resultFullBaconNumber =  tx.run(
													"MATCH p=shortestPath((a:actor {id:\"nm0000102\"})-[*]-(b:actor {id:$aId}))" + 
													"return length(p)",
							parameters( "aId", actorId) );               	
                	StatementResult resultBaconPath =  tx.run(
                									"MATCH p=shortestPath((a:actor {id:\"nm0000102\"})-[*]-(b:actor {id:$aId}))" + 
                									"return  [n In nodes (p) | n.Id]",
                            parameters( "aId", actorId) );

                                     

                	JSONObject bPJO = new JSONObject();
                	List <JSONObject> myBPath = new ArrayList <JSONObject>();
                	List <Record> myBPList = resultBaconPath.list();
                	

                	int size = resultFullBaconNumber.single().get(0).asInt();
                
					try {
						bPJO.accumulate("baconNumber",resultBaconNumber.single().get(0).asInt()-1);
						for (int i=0;i<(size/2);i++) {
							JSONObject temp = new JSONObject();
							temp.accumulate("actorId", myBPList.get(0).get(0).get(0+(i*2)).asObject());
							temp.accumulate("movieId", myBPList.get(0).get(0).get(1+(i*2)).asObject());
							myBPath.add(temp);
						}
						Collections.reverse(myBPath);
						bPJO.accumulate("baconPath", myBPath);
						
						 

						
					} catch (NoSuchRecordException e) {
						//e.printStackTrace();
						throw e;
					} catch (JSONException e) {
						e.printStackTrace();
					}

                    return bPJO;
                }
            } );

        } catch (NoSuchRecordException e) {
			//e.printStackTrace();
			throw e;
        }
		return baconPath;
   }
    


}