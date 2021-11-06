package ins.hands.unid

import android.graphics.BitmapFactory
import android.util.Log
import android.widget.ImageView
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ins.hands.unid.data.MatchingStatusData
import ins.hands.unid.databinding.MatchCardWaitApplyBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.KoinComponent
import org.koin.core.inject
import java.net.HttpURLConnection
import java.net.URL

class MatchWaitViewModel : ViewModel(), KoinComponent {
    val dataSource : RemoteDateSourcePlace by inject()
    var matchingList = MutableLiveData(mutableListOf<MatchingStatusData>())
    fun getMatchByPlace(placeid : String){
        viewModelScope.launch {
            matchingList.value=  dataSource.getMatchingSearch(placeid).data

        }

    }

    fun getProfileImage(image: ImageView, url: String) {
        viewModelScope. launch{

            val imgUrl = URL(url)
            val connection = imgUrl.openConnection() as HttpURLConnection
            connection.setDoInput(true) //url로 input받는 flag 허용
            CoroutineScope(Dispatchers.IO).launch {
                connection.connect() //연결

                val inputStream = connection.getInputStream() // get inputstream
                val decode = BitmapFactory.decodeStream(inputStream)
                CoroutineScope(Dispatchers.Main).launch{
                    image.setImageBitmap(decode)
                }
                Log.d("UrlToBitmap", "Finished")
            }
        }
    }

    fun getPlaceDataById(bind: MatchCardWaitApplyBinding, id: String) {
        viewModelScope.launch {
            dataSource.getPlace(id).apply{
                bind.placeTitle = data.name
                bind.placeAddress = data.address
            }
        }
    }

    fun joinMatch(id: Int) {
        viewModelScope.launch{
            dataSource.joinMatching(MatchingIdClass(id)).apply{

            }
        }
    }

    fun createMatch(placeId: String, message: String, it: MyTimeData) {
        viewModelScope.launch{
            dataSource.createMatching(CreateMatchBody(placeId,"${it.year.t()}-${it.month.t()}-${it.day.t()}T${it.hour.t()}:${it.minute.t()}:00",message)).apply{

            }
        }
    }
    fun Int.t():String{
        return "%2d".format(this)
    }
}