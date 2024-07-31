package com.insight;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import com.insight.states.Question;

import java.util.ArrayList;

public final class Database {
	private static final String URL = "jdbc:mysql://localhost:3306/insight";
	private static Database instance = new Database();
	private Connection connection;
	
	private Database() {
		try {
            this.connection = DriverManager.getConnection(URL, "root", "Bucur3st1");
        } catch (SQLException e) {
            e.printStackTrace();
        }
	}
	
	public void setStarted(final String id) {
		try {
			var pstmt = this.connection.prepareStatement("update rooms set started = ? where id = ?");
			pstmt.setBoolean(1, true);
			pstmt.setString(2, id);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void removeQuestions(final String id) {
		if(id == null) return;
		
		try {
			var pstmt = this.connection.prepareStatement("delete from questions where id_room = ?");
			pstmt.setString(1, id);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void removeJoined(final String id) {
		if(id == null) return;
		
		try {
			var pstmt = this.connection.prepareStatement("delete from joined where id_room = ?");
			pstmt.setString(1, id);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public boolean removeRoom(final String id) {
		if(id == null) return false;
		
		removeQuestions(id);
		removeJoined(id);
		
		try {
			var pstmt = this.connection.prepareStatement("delete from rooms where id = ?");
			pstmt.setString(1, id);
			return pstmt.executeUpdate() > 0;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return false;
	}
	
	public boolean searchRoom(final String id) {
		if(id == null) return false;
		
		try {
			var pstmt = this.connection.prepareStatement("select * from rooms where id = ?");
			pstmt.setString(1, id);
			var rset = pstmt.executeQuery();
			return rset.next() && rset.getString(1).equals(id);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return false;
	}
	
	public boolean roomStarted(final String id) {
		if(id == null) return false;
		
		try {
			var pstmt = this.connection.prepareStatement("select * from rooms where id = ?");
			pstmt.setString(1, id);
			var rset = pstmt.executeQuery();
			if(rset.next()) {
				return rset.getBoolean(2);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return false;
	}
	
	public void setJoinable(final String id) {
		try {
			var pstmt = this.connection.prepareStatement("update rooms set joinable = ? where id = ?");
			pstmt.setBoolean(1, true);
			pstmt.setString(2, id);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void dummy() {
		try {
			var stmt = this.connection.createStatement();
			stmt.executeQuery("select 'dummy'");
		} catch(SQLException e) {
			e.printStackTrace();
		}
	}
	
	public Question questionFor(final String id) {
		int cquest = 0;
		
		try {
			var pstmt = this.connection.prepareStatement("select * from rooms where id = ?");
			pstmt.setString(1, id);
			var rset = pstmt.executeQuery(); 
			if(rset.next()) cquest = rset.getInt("cquest");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		try {
			var pstmt = this.connection.prepareStatement("select * from questions where id_room = ?");
			pstmt.setString(1, id);
			var rset = pstmt.executeQuery(); 
			for(int i = 0; i <= cquest; ++i) {
				if(!rset.next()) return null;
			}
			
			var q = Question.with(rset.getString("quiz"))
                    .addChoice(rset.getString("choice1"))
                    .addChoice(rset.getString("choice2"))
                    .addChoice(rset.getString("choice3"))
                    .addChoice(rset.getString("choice4"));
			q.setAnswer(rset.getInt("answer") - 1);
			return q;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public int placeFor(final String user, final String id) {
		var list = new ArrayList<String>();
		
		try {
			var pstmt = this.connection.prepareStatement("select * from joined where id_room = ? order by score desc");
			pstmt.setString(1, id);
			var rset = pstmt.executeQuery();
			while(rset.next()) {
				list.add(rset.getString("username"));
			}
			
			return list.indexOf(user) + 1;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return 1;
	}
	
	public List<Integer> getScores(final String id) {
		var list = new ArrayList<Integer>();
		
		try {
			var pstmt = this.connection.prepareStatement("select * from joined where id_room = ? order by score desc limit 3");
			pstmt.setString(1, id);
			var rset = pstmt.executeQuery();
			while(rset.next()) {
				list.add(rset.getInt("score"));
			}
			
			return list;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return list;
	}
	
	public boolean wasKicked(final String name) {
		if(name == null) return false;
		
		try {
			var pstmt = this.connection.prepareStatement("select * from joined where username = ?");
			pstmt.setString(1, name);
			var rset = pstmt.executeQuery();
			return !rset.next();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return false;
	}
	
	public void playerJoin(final String id, final Player player) {
		try {
			var pstmt = this.connection.prepareStatement("insert into joined (username, avatar, id_room) values (?, ?, ?)");
			pstmt.setString(1, player.username);
			pstmt.setInt(2, player.avatar);
			pstmt.setString(3, id);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void kickPlayer(final int id) {
		try {
			var pstmt = this.connection.prepareStatement("delete from joined where id = ?");
			pstmt.setInt(1, id);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public boolean roomFinished(final String id) {
		try {
			var pstmt = this.connection.prepareStatement("select * from questions where id_room = ?");
			pstmt.setString(1, id);
			var rset = pstmt.executeQuery();
			int num = 0;
			while(rset.next()) {
				++num;
			}
			
			pstmt.close();
			pstmt = this.connection.prepareStatement("select cquest from rooms where id = ?");
			pstmt.setString(1, id);
			rset = pstmt.executeQuery(); rset.next();
			int cquest = rset.getInt(1);
			return cquest >= num;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return false;
	}
	
	public void nextQuestion(final String id) {
		try {
			var pstmt = this.connection.prepareStatement("update rooms set cquest = cquest + 1 where id = ?");
			pstmt.setString(1, id);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public List<Player> getPlayers(final String id) {
		List<Player> players = new ArrayList<Player>();
		
		try {
			var pstmt = this.connection.prepareStatement("select * from joined where id_room = ?");
			pstmt.setString(1, id);
			var rset = pstmt.executeQuery();
			while(rset.next()) {
				var player = new Player(rset.getString(2), rset.getInt(3));
				player.id = rset.getInt(1);
				players.add(player);
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return players;
	}
	
	public void addQuestions(final String id, final List<Question> questions) {
		if(id == null) return;
		
		try {
			for (var question : questions) {
				var pstmt = this.connection.prepareStatement("insert into questions (quiz, choice1, choice2, choice3, choice4, answer, id_room) values (?, ?, ?, ?, ?, ?, ?)");
				pstmt.setString(1, question.text);
				pstmt.setString(2, question.choices.get(0));
				pstmt.setString(3, question.choices.get(1));
				pstmt.setString(4, question.choices.get(2));
				pstmt.setString(5, question.choices.get(3));
				pstmt.setInt(6, question.answer);
				pstmt.setString(7, id);
				
				pstmt.executeUpdate();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public boolean roomJoinable(final String id) {
		if(id == null) return false;
		
		try {
			var pstmt = this.connection.prepareStatement("select * from rooms where id = ?");
			pstmt.setString(1, id);
			var rset = pstmt.executeQuery();
			if(rset.next()) {
				return rset.getBoolean(3) && !rset.getBoolean(2);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return false;
	}
	
	public void setTimer(final String id, final int timer) {
		try {
			var pstmt = this.connection.prepareStatement("update rooms set timer = ? where id = ?");
			pstmt.setInt(1, timer);
			pstmt.setString(2, id);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public int getTimer(final String id) {
		try {
			var pstmt = this.connection.prepareStatement("select timer from rooms where id = ?");
			pstmt.setString(1, id);
			var rset = pstmt.executeQuery(); rset.next();
			return rset.getInt(1);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return 0;
	}
	
	public void addScore(final String user, final String id) {
		try {
			var pstmt = this.connection.prepareStatement("update joined set score = score + 700 where id_room = ? and username = ?");
			pstmt.setString(1, id);
			pstmt.setString(2, user);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public List<String> top3(final String id) {
		var list = new ArrayList<String>();
		
		try {
			var pstmt = this.connection.prepareStatement("select * from joined where id_room = ? order by score desc limit 3");
			pstmt.setString(1, id);
			var rset = pstmt.executeQuery();
			while(rset.next()) {
				list.add(rset.getString("username"));
			}
			
			return list;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return list;
	}
	
	public void createRoom(final String id) {
		try {
			var pstmt = this.connection.prepareStatement("insert into rooms (id, started, joinable) values (?, ?, ?)");
			pstmt.setString(1, id);
			pstmt.setBoolean(2, false);
			pstmt.setBoolean(3, false);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public List<String> getRoomsIDs() {
		var list = new ArrayList<String>();
		
		try {
			var stmt = this.connection.createStatement();
			var rset = stmt.executeQuery("select id from rooms");
			while (rset.next()) {
				list.add(rset.getString(1));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return list;
	}
	
	public Statement statement() {
		try {
			return this.connection.createStatement();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public static Database get() {
		return instance;
	}
}
