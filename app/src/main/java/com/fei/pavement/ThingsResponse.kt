package com.fei.pavement


data class SearchPage(val total:Int,val list:List<SearchListPage>,
                      val pageNum:Int,val pageSize:Int,val size:Int,
                      val startRow:Int,val endRow:Int,val pages:Int,
                      val prePage:Int,val nextPage:Int,val isFirstPage:Boolean,
                      val isLastPage:Boolean,val hasPreviousPage:Boolean,val hasNextPage:Boolean,
                      val navigatePages:Int,val navigatepageNums:List<Int>,val navigateFirstPage:Int,
                      val navigateLastPage:Int
)
data class SearchListPage(val id:Int,val latitude:String,val longitude:String,val createTime:String)

//{
//    "total": 7,
//    "list": [
//    {
//        "id": 1,
//        "latitude": "21.5",
//        "longitude": "12",
//        "createTime": "2020-10-20 17:50:39"
//    },
//    {
//        "id": 2,
//        "latitude": "13",
//        "longitude": "55",
//        "createTime": "2020-10-20 17:53:28"
//    }
//    ],
//    "pageNum": 1,
//    "pageSize": 2,
//    "size": 2,
//    "startRow": 1,
//    "endRow": 2,
//    "pages": 4,
//    "prePage": 0,
//    "nextPage": 2,
//    "isFirstPage": true,
//    "isLastPage": false,
//    "hasPreviousPage": false,
//    "hasNextPage": true,
//    "navigatePages": 8,
//    "navigatepageNums": [
//    1,
//    2,
//    3,
//    4
//    ],
//    "navigateFirstPage": 1,
//    "navigateLastPage": 4
//}