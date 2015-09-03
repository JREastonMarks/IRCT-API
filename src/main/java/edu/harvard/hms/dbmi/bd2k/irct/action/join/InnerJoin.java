/*
 *  This file is part of Inter-Resource Communication Tool (IRCT).
 *
 *  IRCT is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  IRCT is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with IRCT.  If not, see <http://www.gnu.org/licenses/>.
 */
package edu.harvard.hms.dbmi.bd2k.irct.action.join;

import java.util.Map;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import edu.harvard.hms.dbmi.bd2k.irct.model.result.Joinable;
import edu.harvard.hms.dbmi.bd2k.irct.model.result.MemoryResultSet;
import edu.harvard.hms.dbmi.bd2k.irct.model.result.ResultSet;
import edu.harvard.hms.dbmi.bd2k.irct.model.result.exception.ResultSetException;

/**
 * Performs a inner join between two result sets that implements the
 * Joinable interface
 * 
 * NOTE: Currently returns a MemoryResultSet which may limit the size of joins
 * that can be performed
 * 
 * @author Jeremy R. Easton-Marks
 *
 */
public class InnerJoin implements JoinAction {
	ResultSet rs1;
	int rs1MatchIndex;
	ResultSet rs2;
	int rs2MatchIndex;

	ResultSet results;

	public void setup(Map<String, String> parameters) {
		
	}
	
	public void setJoins(Joinable...joinables ) throws Exception{
		Joinable rs1 = joinables[0];
		Joinable rs2 = joinables[1];
		if (rs1 instanceof ResultSet) {
			this.rs1MatchIndex = rs1.getMatchColumnIndexes()[0];
			this.rs1 = (ResultSet) rs1;
		} else {
			throw new ResultSetException("RS1 Not an instance of ResultSet");
		}

		if (rs1 instanceof ResultSet) {
			this.rs2MatchIndex = rs2.getMatchColumnIndexes()[0];
			this.rs2 = (ResultSet) rs2;
		} else {
			throw new ResultSetException("RS2 Not an instance of ResultSet");
		}
	}

	public void run() {
		try {
			MemoryResultSet computedResults = new MemoryResultSet();
			computedResults.appendColumn(rs1.getColumn(rs1MatchIndex));

			for (int rsColumnIterator = 0; rsColumnIterator < rs1
					.getColumnSize(); rsColumnIterator++) {
				if (rsColumnIterator != rs1MatchIndex) {
					computedResults.appendColumn(rs1
							.getColumn(rsColumnIterator));
				}
			}
			for (int rsColumnIterator = 0; rsColumnIterator < rs2
					.getColumnSize(); rsColumnIterator++) {
				if (rsColumnIterator != rs2MatchIndex) {
					computedResults.appendColumn(rs2
							.getColumn(rsColumnIterator));
				}
			}
			int baseColumn = rs1.getColumnSize() - 1;
					
			rs1.beforeFirst();
			while (rs1.next()) {
				Object rs1RowMatchObj = ((Joinable) rs1).getObject(rs1MatchIndex);
				rs2.beforeFirst();
				while (rs2.next()) {
					if (((Joinable) rs2).getObject(rs1MatchIndex).equals(rs1RowMatchObj)) {
						//Add a new row
						computedResults.appendRow();
						//Set the join column value
						computedResults.updateObject(rs1MatchIndex, rs1RowMatchObj);

						//Copy RS1 values over
						for (int rsColumnIterator = 0; rsColumnIterator < rs1
								.getColumnSize(); rsColumnIterator++) {
							
							if (rsColumnIterator != rs1MatchIndex) {
								computedResults.updateObject(rsColumnIterator, ((Joinable) rs1).getObject(rsColumnIterator)); 
							}
						}
						
						//Copy RS2 values over
						for (int rsColumnIterator = 0; rsColumnIterator < rs2
								.getColumnSize(); rsColumnIterator++) {
							if (rsColumnIterator != rs2MatchIndex) {
								computedResults.updateObject(baseColumn + rsColumnIterator, ((Joinable) rs2).getObject(rsColumnIterator));
							}
						}

					}
				}
			}
			computedResults.beforeFirst();
			this.results = computedResults;
		} catch (ResultSetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public ResultSet getResults() {
		return this.results;
	}
	
	public String getType() {
		return "Inner Join";
	}

	
	public JsonObject toJson() {
		return toJson(1);
	}

	public JsonObject toJson(int depth) {
		depth--;
		
		JsonObjectBuilder jsonBuilder = Json.createObjectBuilder();
		jsonBuilder.add("type", getType());
		jsonBuilder.add("rs1MatchIndex", this.rs1MatchIndex);
		jsonBuilder.add("rs2MatchIndex", this.rs2MatchIndex);
		
		return jsonBuilder.build();
	}

}
