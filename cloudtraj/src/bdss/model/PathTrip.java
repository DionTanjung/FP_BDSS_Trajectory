package bdss.model;

import java.util.ArrayList;
import java.util.List;

import com.microsoft.azure.storage.table.TableServiceEntity;

public class PathTrip extends TableServiceEntity {
	private String TRIP_ID;
	private char CALL_TYPE;
	private int ORIGIN_CALL;
	private int ORIGIN_STAND;
	private int TAXI_ID;
	private int TIMESTAMP; // Unix Timestamp
	private char DAY_TYPE;
	private boolean MISSING_DATA;
	private String POLYLINE;
	
	public PathTrip() {
		
	}
	
	public PathTrip(String tRIP_ID, int tAXI_ID, int tIMESTAMP, char dAY_TYPE, boolean mISSING_DATA,
			String pOLYLINE) {
		super();
		this.TRIP_ID = tRIP_ID;
		this.TAXI_ID = tAXI_ID;
		this.TIMESTAMP = tIMESTAMP;
		this.DAY_TYPE = dAY_TYPE;
		this.MISSING_DATA = mISSING_DATA;
		this.POLYLINE = pOLYLINE;
	}

	public PathTrip(String tRIP_ID, int tAXI_ID, int tIMESTAMP, String pOLYLINE) {
		super();
		this.TRIP_ID = tRIP_ID;
		this.TAXI_ID = tAXI_ID;
		this.TIMESTAMP = tIMESTAMP;
		this.POLYLINE = pOLYLINE;
	}

	public PathTrip(String tAXI_ID, String tIMESTAMP, String pOLYLINE) {
		super();
		this.partitionKey = tIMESTAMP;
		this.rowKey = tAXI_ID;
		this.POLYLINE = pOLYLINE;
	}

	public String getPOLYLINE() {
		return POLYLINE;
	}

	public void setPOLYLINE(String pOLYLINE) {
		POLYLINE = pOLYLINE;
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return this.partitionKey+", "+this.rowKey+", "+this.POLYLINE;
	}
}
