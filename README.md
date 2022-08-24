# hdl-job
一个定时任务组件
##模块说明
        hdl-job-admin 任务服务管理模块，包含一个admin后台管理界面
        hdl-job-common 任务服务端通用库，包含关键任务接口定义
        hdl-job-client-common  客户端任务通用库，包含客户端的接口定义
        hdl-job-feign Feign组件，如果admin模块需要使用Feign作为任务调用方式时，需要引入该模块
        hdl-job-feign-client组件，如果admin使用feign作为任务调度方式，那么客户端需要引入该模块
        hdl-job-lib 项目公用库，一些通用的类，可以放在这个组件中
