package AnswerModel;

import DB.Database;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.jws.*;

/**
 *
 * @author M. Fauzan Naufan
 */
@WebService(serviceName = "AnswerWS")
public class AnswerWS {

    /**
     * Web service operation
     *
     * @param qid
     * @return
     */
    @WebMethod(operationName = "getAnswerByQID")
    @WebResult(name = "Answer")
    public ArrayList<Answer> getAnswerByQID(@WebParam(name = "qid") int qid) {
        Connection conn = new Database().connect();
        ArrayList<Answer> answers = new ArrayList();
        Statement stmt;
        ResultSet rs;
        
        try {
            stmt = conn.createStatement();
            String sql = "SELECT * FROM answer WHERE qid = ?";
            
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1,qid);
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                answers.add(new Answer(rs.getInt("AID"),
                            rs.getInt("UserID"),
                            rs.getString("content"),
                            rs.getInt("Votes"),
                            qid,
                            rs.getString("DateTime")
                            ));
            }
            rs.close();
            stmt.close();
        } catch (SQLException ex) {
            
        }
        return answers;
    }

    /**
     * Web service operation
     * @param access_token
     * @param qid
     * @param content
     * @return 
     */
    @WebMethod(operationName = "createAnswer")
    public String createAnswer(@WebParam(name = "access_token") String access_token, @WebParam(name = "qid") int qid, @WebParam(name = "content") String content) {
        Connection conn = new Database().connect();
        Statement stmt;
        //1.HTTP Request connection ke Identity Service, untuk memastikan pemilik access_token
        //2a.Jika access token kadaluarsa, respons expired token
        //2b.Jika access token tidak valid, respons error
        //2c.Jika access token valid, ambil user ID
        int userID = 0;
        try {
            stmt = conn.createStatement();
            String sqlA = "INSERT INTO answer VALUES (0,?,?,0,?,?)";
            String sqlQ = "UPDATE question SET Answers=Answers+1 WHERE qid = ?";
            PreparedStatement pstmtA = conn.prepareStatement(sqlA);
            PreparedStatement pstmtQ = conn.prepareStatement(sqlQ);
            pstmtA.setInt(1, userID);
            pstmtA.setString(2, content);
            pstmtA.setInt(3, qid);
            SimpleDateFormat ft = new SimpleDateFormat("y-M-d H:m:s");
            pstmtA.setString(4, ft.format(new java.util.Date()));
            pstmtQ.setInt(1, qid);
            int a = pstmtA.executeUpdate();
            int b = pstmtQ.executeUpdate();
            stmt.close();
            return "Respons oke!";
        } catch (SQLException se) {
            return "Gagal!";
        }
    }
}
