class NeticaApi:
用法见test.com.bnAnalysis.NeticaApiTest


Map<String, String[]> getRangeMap()
获得每个变量的离散化区间


void printRangeMap()
打印离散化区间


void buildNet(String original_csv, int num_thread, int num_bins)

建立贝叶斯网络，参数：输入文件，banjo运行线程数，离散化区间数(使每个区间数据频数趋于相等)。



void loadNet()

读入建立的 netica_out_dir/Learned_netica.dne 和 netica_out_dir/range_list.csv。


List<String> getChildren(String node_name)
获得孩子


void printChildren(String node_name)
打印孩子


double getInfer(String conds, String target)
获得条件概率