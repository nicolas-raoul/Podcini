package ac.mdiq.podcini.ui.statistics.downloads


import ac.mdiq.podcini.R
import ac.mdiq.podcini.databinding.StatisticsFragmentBinding
import ac.mdiq.podcini.storage.DBReader
import ac.mdiq.podcini.storage.DBReader.StatisticsResult
import ac.mdiq.podcini.storage.StatisticsItem
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

/**
 * Displays the 'download statistics' screen
 */
class DownloadStatisticsFragment : Fragment() {
    private var disposable: Disposable? = null
    private var downloadStatisticsList: RecyclerView? = null
    private var progressBar: ProgressBar? = null
    private var listAdapter: DownloadStatisticsListAdapter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?
    ): View {
        val binding = StatisticsFragmentBinding.inflate(inflater)
//        val root = inflater.inflate(R.layout.statistics_fragment, container, false)
        downloadStatisticsList = binding.statisticsList
        progressBar = binding.progressBar
        listAdapter = DownloadStatisticsListAdapter(requireContext(), this)
        downloadStatisticsList?.setLayoutManager(LinearLayoutManager(context))
        downloadStatisticsList?.setAdapter(listAdapter)
        refreshDownloadStatistics()

        return binding.root
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        menu.findItem(R.id.statistics_reset).setVisible(false)
        menu.findItem(R.id.statistics_filter).setVisible(false)
    }

    private fun refreshDownloadStatistics() {
        progressBar!!.visibility = View.VISIBLE
        downloadStatisticsList!!.visibility = View.GONE
        loadStatistics()
    }

    private fun loadStatistics() {
        if (disposable != null) {
            disposable!!.dispose()
        }

        disposable =
            Observable.fromCallable {
                // Filters do not matter here
                val statisticsData = DBReader.getStatistics(false, 0, Long.MAX_VALUE)
                statisticsData.feedTime.sortWith { item1: StatisticsItem, item2: StatisticsItem ->
                    item2.totalDownloadSize.compareTo(item1.totalDownloadSize)
                }
                statisticsData
            }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ result: StatisticsResult ->
                    listAdapter!!.update(result.feedTime)
                    progressBar!!.visibility = View.GONE
                    downloadStatisticsList!!.visibility = View.VISIBLE
                }, { error: Throwable? -> Log.e(TAG, Log.getStackTraceString(error)) })
    }

    companion object {
        private val TAG: String = DownloadStatisticsFragment::class.java.simpleName
    }
}
