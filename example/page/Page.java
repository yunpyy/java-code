package example.page;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

 
/**
 * 
 */
 
/**
 * @author 管进峰
 *
 */
public class Page<T> {
    private int pageTotal;      //总页数
    private int pageSize=30;    //每页条数
    private int currentPage=1;  //当前页
    private int total;          //总条数
    private int pageShowSize=5; //分页编号显示个数
     
     
    public int getPageShowSize() {
        return pageShowSize;
    }
 
    public void setPageShowSize(int pageShowSize) {
        this.pageShowSize = pageShowSize;
    }
 
    public int getTotal() {
        return total;
    }
 
    public void setTotal(int total) {
        this.total = total;
    }
 
    public void setPageTotal(int pageTotal) {
        this.pageTotal = pageTotal;
    }
    public int getPageTotal() {
        return total%pageSize==0?(total/pageSize):(total/pageSize)+1;
    }
     
    public int getPageSize() {
        return pageSize;
    }
 
    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }
 
 
    public int getCurrentPage() {
        return currentPage;
    }
 
 
    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }
 
 
    /**
     * @param args
     */
    /**
     * @param args
     * @throws IOException 
     * @throws NumberFormatException 
     */
    public static void main(String[] args) throws NumberFormatException, IOException {
        Page<User> page=new Page<User>();
        List<User> users=page.init();
        System.out.println("欢迎进入控制台分页。。");
        System.out.println("=======================================");
//      System.out.println("第一页：");
//      System.out.println("page.getPageTotal():"+page.getPageTotal());
//      for(User uu_:page.frist(users,page)){
//          System.out.println("编号："+uu_.getId()+"  名称："+uu_.getName());
//      }
//      System.out.println("最后页：");
         
//      for(User uu_1:page.last(users,page)){
//          System.out.println("编号："+uu_1.getId()+"  名称："+uu_1.getName());
//      }
        System.out.println("请设置每页显示条数");
        page.setTotal(users.size());    //总记录数
        BufferedReader strin=new BufferedReader(new InputStreamReader(System.in));
        int tiaoshu=Integer.parseInt(strin.readLine());
        page.setPageSize(tiaoshu);
        System.out.println("=======================================");
        boolean flag=true;
        do {
            int pageZongshu=page.getPageTotal();
            System.out.println("page.getPageTotal():"+page.getPageTotal());
            for(int i=1;i<=pageZongshu;i++){
                System.out.print(i+" ");
            }
            System.out.println("\n当前要前往那一页");
            BufferedReader strin1=new BufferedReader(new InputStreamReader(System.in));
            int toPage=Integer.parseInt(strin1.readLine());
            page.setCurrentPage(toPage);            //当前页
             
            List<User> showUser=page.showUser(page, users);
            for(User u:showUser){
                System.out.println("编号："+u.getId()+"  名称："+u.getName());
            }
            System.out.println("==============================================");
            System.out.println("page.getCurrentPage():"+page.getCurrentPage());
            List<Integer> ids=page.nearPageNum(page);
            for(int k=0;k<ids.size();k++){
                System.out.print(ids.get(k)+" ");
            }
            if(ids.get(ids.size()-1)<page.getPageTotal()){
                System.out.print("  ..."+page.getPageTotal());
            }
            System.out.println("\n==============================================");
            System.out.println("是否继续？（Y/N）");
            BufferedReader strin2=new BufferedReader(new InputStreamReader(System.in));
            String con=String.valueOf(strin2.readLine());
            if(con.toUpperCase().equals("N")){
                flag=false;
            }
        } while (flag);
    }
 
    public List<User> showUser(Page page,List<User> users){
        List<User> users_=new ArrayList<User>();
        int startNum=(page.currentPage-1)*page.getPageSize();
        int endNum=page.currentPage*page.getPageSize()>page.total?page.total:page.currentPage*page.getPageSize();
        for(int i=startNum;i<endNum;i++){
            User u=users.get(i);
            users_.add(u);
        }
        return users_;
    }
 
    public List<T> frist(List<T> users,Page page){
        List<T> tt=new ArrayList<T>();
        for(int i=0;i<page.pageSize;i++){
            tt.add(users.get(i));
        }
        return tt;
    }
     
    public List<T> last(List<T> users,Page page){
        List<T> tt=new ArrayList<T>();
        int start=(page.getPageTotal()-1)*page.getPageSize();
        int end=page.getPageTotal()*page.getPageSize()>page.getTotal()?page.getTotal():page.getPageTotal()*page.getPageSize();
        for(int i=start;i<end;i++){
            tt.add(users.get(i));
        }
        return tt;
    }
    public List<User> init(){
        List<User> users=new ArrayList<User>();
        for(int i=1;i<100;i++){
            users.add(new User(i,"八戒"+i));
        }
        return users;
    }
     
    public List<Integer> nearPageNum(Page page){
        List<Integer> pageIds=new ArrayList<Integer>();
        int num=page.getCurrentPage();
        int total=page.getPageTotal();
        int showSize=page.getPageShowSize();
        if(total==num){
            if(total<showSize){
                pageIds.clear();
                for(int i=1;i<=total;i++){
                    pageIds.add(i);
                }
            }else{
                pageIds.clear();
                for(int i=total-showSize+1;i<=total;i++){
                    pageIds.add(i);
                }
            }
        }else if(total>num){
            pageIds.clear();
            if(num<showSize){
                for(int i=1;i<=showSize;i++){
                    pageIds.add(i);
                }
            }else{
                for(int i=num-2;i<=num+2;i++){
                    pageIds.add(i);
                }
            }
        }
        return pageIds;
    }
}