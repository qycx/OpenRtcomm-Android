package ext;

import androidx.fragment.app.Fragment;

import com.qycx.Fragment_null;
import com.qycx.base.util.Log;
import com.qysd.base.MapExtTmpl;

public class myMapExt extends MapExtTmpl {


    @Override
    public boolean bExists_locDataReq()
    {
        return true;
    }



    @Override
    public int Application_onCreate_init()
    {
        //
        return  0;
    }


    //
    @Override
    public Fragment new_Fragment_map()
    {
        //
        Log.d("at","need to return real Fragment_map");
        //
        return  new Fragment_null();

        //  return new Fragment_map();
    }



    @Override
    public int sendLocation(Object p0, String locStr, long imGrp_related_ui64Id) {
        return 0;
    }


    @Override
    public int onRecv_locations(CParam_onRecv_locations param) {

        return 0;
    }

    @Override
    public int sendMark(String markStr, long imGrp_related_ui64Id) {
        return 0;
    }

    @Override
    public int onRecv_marks(Param_onRecv_marks param) {
        return 0;
    }

    @Override
    public int onRecv_plottings(Param_onRecv_plottings param) {
        return 0;
    }

    //




}
