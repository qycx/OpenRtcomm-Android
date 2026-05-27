package ext;

import androidx.fragment.app.Fragment;

import com.qycx.Fragment_null;
import com.qycx.base.util.Log;
import com.qysd.base.mapExtTmpl;

public class myMapExt extends mapExtTmpl {

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
    public int sendLocation(String locStr, long imGrp_related_ui64Id) {
        return 0;
    }

    @Override
    public int onRecv_locations(Param_onRecv_locations param) {
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
