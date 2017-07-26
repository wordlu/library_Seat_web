package com.xoqao.web.controller;

import com.xoqao.web.bean.admin.Admin;
import com.xoqao.web.bean.booking.Booking;
import com.xoqao.web.bean.booking.BookingSeat;
import com.xoqao.web.bean.booking.SeatBookings;
import com.xoqao.web.bean.building.Building;
import com.xoqao.web.bean.floors.Floor;
import com.xoqao.web.bean.news.Notice;
import com.xoqao.web.bean.user.User;
import com.xoqao.web.commen.CommenValue;
import com.xoqao.web.bean.news.News;
import com.xoqao.web.bean.seat.Seat;
import com.xoqao.web.bean.userbook.UserLearn;
import com.xoqao.web.service.*;
import com.xoqao.web.utils.DateUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by 1Q84 on 2017/7/10.
 */
@Controller
@RequestMapping("/jsp")
public class admin_pageController {
    @Autowired
    private UserLearnService userLearnService;


    @Autowired
    private SeatService seatService;
    @Autowired
    private UserService userService;

    @Autowired
    private NoticeService noticeService;


    @Autowired
    private BookingService bookingService;

    @RequestMapping("/index_Admin")
    public String index_Admin(Model model, HttpSession httpSession) throws Exception {
        Integer type = (Integer) httpSession.getAttribute("admintype");
        if (type == 1) {
            Floor floorBycount = (Floor) httpSession.getAttribute("admin");
            return "admin_page/Index_Admin";
        } else if (type == 2) {
            Building buildAdminByCount = (Building) httpSession.getAttribute("admin");
            return "buildingadmin_page/Index_BuildingAdmin";
        } else if (type == 3) {
            Admin adminByCount = (Admin) httpSession.getAttribute("admin");
            return "superadmin_page/Index_SuperAdmin";
        }
        return "admin_page/Index_Admin";
    }

    @RequestMapping("/main_Admin")
    public String main_Admin(Model model) throws Exception {
        List<Notice> allNoticetop = noticeService.findAllNoticetop();
        model.addAttribute("noticeTop", allNoticetop);
        return "admin_page/Main_Admin";
    }

    @RequestMapping("/seat_In_Use")
    public String seat_In_Use(Model model, Integer page, HttpSession httpSession) throws Exception {
        int pageSize = 5;

        Floor floor = (Floor) httpSession.getAttribute("admin");
        List<BookingSeat> seatInSeat = bookingService.findSeatInSeat(floor.getFid());
        if (seatInSeat.size() > 0) {
            model.addAttribute("inSeatSize", seatInSeat.size());
            int pageTims;
            if (seatInSeat.size() % pageSize == 0) {
                pageTims = seatInSeat.size() / pageSize;
            } else {
                pageTims = seatInSeat.size() / pageSize + 1;
            }
            httpSession.setAttribute("pageTimes", pageTims);
            //页面初始的时候没有初试值
            if (null == page) {
                page = 1;
            }
            //每页开始的第几条记录
            int startRow;
            if (seatInSeat.size() < pageSize) {
                startRow = 0;
            } else {
                startRow = (page - 1) * pageSize;
            }
            model.addAttribute("currentPage", page);
            List<BookingSeat> seatInSeatPage = bookingService.findSeatInSeatPage(floor.getFid(), startRow, pageSize);
            model.addAttribute("inseats", seatInSeatPage);
        } else {
            model.addAttribute("nullList", "暂无入座数据");
        }
        return "admin_page/Seat_In_Use";

    }

    /**
     * 查看当前已预约的座位信息状况
     *
     * @param model
     * @param page
     * @param httpSession
     * @return
     * @throws Exception
     */
    @RequestMapping("/seat_In_Book")
    public String seat_In_Book(Model model, Integer page, HttpSession httpSession) throws Exception {
        Integer pageSize = 6;
        Floor floor = (Floor) httpSession.getAttribute("admin");
        List<Seat> bookSeat = bookingService.findBookSeat(floor.getFid());
        if (bookSeat.size() > 0) {
            model.addAttribute("bookSeatSize", bookSeat.size());
            int pageTims;
            if (bookSeat.size() % pageSize == 0) {
                pageTims = bookSeat.size() / pageSize;
            } else {
                pageTims = bookSeat.size() / pageSize + 1;
            }
            httpSession.setAttribute("pageTimes", pageTims);
            //页面初始的时候没有初试值
            if (null == page) {
                page = 1;
            }
            //每页开始的第几条记录
            int startRow;
            if (bookSeat.size() < pageSize) {
                startRow = 0;
            } else {
                startRow = (page - 1) * pageSize;
            }
            model.addAttribute("currentPage", page);
            List<Seat> bookSeatpage = bookingService.findBookSeatpage(floor.getFid(), startRow, pageSize);
            List<SeatBookings> seatBookinges = new ArrayList<SeatBookings>();
            for (int i = 0; i < bookSeatpage.size(); i++) {
                SeatBookings seatBookings = new SeatBookings();
                BeanUtils.copyProperties(bookSeatpage.get(i), seatBookings);
                List<Booking> bookSeatBooking = bookingService.findBookSeatBooking(bookSeatpage.get(i).getSid());
                seatBookings.setBookings(bookSeatBooking);
                seatBookinges.add(seatBookings);
            }
            model.addAttribute("seatbooks", seatBookinges);
        } else {
            httpSession.setAttribute("pageTimes", 1);
            model.addAttribute("nullList", "暂无数据");
        }
        return "admin_page/Seat_In_Book";

    }

    /**
     * 查看当天空闲座位
     *
     * @param model
     * @param page
     * @param httpSession
     * @return
     * @throws Exception
     */
    @RequestMapping("/seat_In_Empty")
    public String seat_In_Empty(Model model, Integer page, HttpSession httpSession) throws Exception {

        Integer pageSize = 6;
        Floor floor1 = (Floor) httpSession.getAttribute("admin");

        List<Seat> canBookingToday = bookingService.findCanBookingToday(floor1.getFid());
        if (canBookingToday.size() > 0) {
            model.addAttribute("seatSize", canBookingToday.size());
            int pageTims;
            if (canBookingToday.size() % pageSize == 0) {
                pageTims = canBookingToday.size() / pageSize;
            } else {
                pageTims = canBookingToday.size() / pageSize + 1;
            }
            httpSession.setAttribute("pageTimes", pageTims);
            //页面初始的时候没有初试值
            if (null == page) {
                page = 1;
            }
            //每页开始的第几条记录
            int startRow;
            if (canBookingToday.size() < pageSize) {
                startRow = 0;
            } else {
                startRow = (page - 1) * pageSize;
            }
            model.addAttribute("currentPage", page);
            List<Seat> canBookingTodayPage = bookingService.findCanBookingTodayPage(floor1.getFid(), startRow, pageSize);
            model.addAttribute("seats", canBookingTodayPage);
        } else {
            httpSession.setAttribute("pageTimes", 1);
            model.addAttribute("nullList", "暂无未预约座位");
        }

        return "admin_page/Seat_In_Empty";
    }

    @RequestMapping("/add_News")
    public String add_News(Model model) throws Exception {
        return "admin_page/Add_News";
    }

    @RequestMapping("/news_List_Admin")
    public String news_List_Admin(Model model, Integer page, HttpSession httpSession) throws Exception {
        Integer pageSize = 5;
        List<Notice> allNotice = noticeService.findAllNotice();
        model.addAttribute("noticeSize", allNotice.size());
        int pageTims;
        if (allNotice.size() % pageSize == 0) {
            pageTims = allNotice.size() / pageSize;
        } else {
            pageTims = allNotice.size() / pageSize + 1;
        }
        httpSession.setAttribute("pageTimes", pageTims);
        //页面初始的时候没有初试值
        if (null == page) {
            page = 1;
        }
        //每页开始的第几条记录
        int startRow;
        if (allNotice.size() < pageSize) {
            startRow = 0;
        } else {
            startRow = (page - 1) * pageSize;
        }
        model.addAttribute("currentPage", page);
        List<Notice> allNoticepage = noticeService.findAllNoticepage(startRow, pageSize);
        if (allNoticepage.size() > 0) {
            model.addAttribute("notices", allNoticepage);
            return "admin_page/News_List_Admin";
        } else {
            model.addAttribute("nullList", "暂无数据");
            return "admin_page/News_List_Admin";
        }
    }

    @RequestMapping("/adSeatBookSub")
    public String adSeatBook(Model model, String seatNum, String sno, String stime, String etime, Integer page, HttpSession httpSession) throws Exception {
        return "admin_page/Seat_In_Empty";
    }

    /**
     * 释放预约
     *
     * @param model
     * @param bid
     * @param page
     * @param httpSession
     * @return
     * @throws Exception
     */
    @RequestMapping("/releaseBook")
    public String releaseBook(Model model, Integer bid, Integer page, HttpSession httpSession) throws Exception {

        return "admin_page/Seat_In_Empty";
    }

    public static HttpSession getSession() {
        HttpSession session = null;
        try {
            session = getRequest().getSession();
        } catch (Exception e) {
        }
        return session;
    }

    public static HttpServletRequest getRequest() {
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return attrs.getRequest();
    }
}
