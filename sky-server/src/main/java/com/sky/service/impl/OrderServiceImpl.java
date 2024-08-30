package com.sky.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.context.BaseContext;
import com.sky.dto.*;
import com.sky.entity.*;
import com.sky.exception.AddressBookBusinessException;
import com.sky.exception.OrderBusinessException;
import com.sky.exception.ShoppingCartBusinessException;
import com.sky.mapper.*;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.OrderService;
import com.sky.utils.WeChatPayUtil;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;
import com.sky.websocket.WebSocketServer;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderDetailMapper orderDetailMapper;
    @Autowired
    private AddressBookMapper addressBookMapper;
    @Autowired
    private ShoppingCartMapper shoppingCartMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private WeChatPayUtil weChatPayUtil;
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private SetmealMapper setmealMapper;
    @Autowired
    private WebSocketServer webSocketServer;

    /**
     * 提交订单
     * @param ordersSubmitDTO
     * @return
     */
    @Transactional
    @Override
    public OrderSubmitVO submitOrder(OrdersSubmitDTO ordersSubmitDTO) {

        //判断有无地址
        AddressBook addressBook = addressBookMapper.getById(ordersSubmitDTO.getAddressBookId());
        if(addressBook == null) {
            throw new AddressBookBusinessException(MessageConstant.ADDRESS_BOOK_IS_NULL);
        }

        //判断购物车是否为空
        Long currentId = BaseContext.getCurrentId();
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setUserId(currentId);
        List<ShoppingCart> list = shoppingCartMapper.list(shoppingCart);
        if(list == null || list.size() == 0) {
            throw new ShoppingCartBusinessException(MessageConstant.SHOPPING_CART_IS_NULL);
        }

        //向order表中插入数据
        Orders orders = new Orders();
        BeanUtils.copyProperties(ordersSubmitDTO, orders);
        orders.setAddressBookId(addressBook.getId());
        orders.setAddress(addressBook.getDetail());
        orders.setOrderTime(LocalDateTime.now());
        orders.setStatus(Orders.PENDING_PAYMENT);
        orders.setPayStatus(Orders.UN_PAID);
        orders.setConsignee(addressBook.getConsignee());
        orders.setPhone(addressBook.getPhone());
        orders.setUserId(currentId);
        orders.setNumber(String.valueOf(System.currentTimeMillis()));

        orderMapper.insert(orders);

        //向订单明细表插入数据
        ArrayList<OrderDetail> orderDetailList = new ArrayList<>();

        list.forEach(cart -> {
            OrderDetail orderDetail = new OrderDetail();
            BeanUtils.copyProperties(cart, orderDetail);
            orderDetail.setOrderId(orders.getId());
            orderDetailList.add(orderDetail);
        });

        //批量插入
        orderDetailMapper.insertBatch(orderDetailList);

        //清空购物车
        shoppingCartMapper.deleteAllShoppingCart(shoppingCart);


        //封装返回对象VO
        OrderSubmitVO orderSubmitVO = OrderSubmitVO.builder().id(orders.getId()).orderTime(orders.getOrderTime()).orderAmount(orders.getAmount()).orderNumber(orders.getNumber()).build();

        return orderSubmitVO;
    }



    /**
     * 订单支付
     *
     * @param ordersPaymentDTO
     * @return
     */
    public OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) throws Exception {
        // 当前登录用户id
        Long userId = BaseContext.getCurrentId();
        User user = userMapper.getById(userId);

//        //调用微信支付接口，生成预支付交易单
//        JSONObject jsonObject = weChatPayUtil.pay(
//                ordersPaymentDTO.getOrderNumber(), //商户订单号
//                new BigDecimal(0.01), //支付金额，单位 元
//                "苍穹外卖订单", //商品描述
//                user.getOpenid() //微信用户的openid
//        );
        JSONObject jsonObject = new JSONObject();

        if (jsonObject.getString("code") != null && jsonObject.getString("code").equals("ORDERPAID")) {
            throw new OrderBusinessException("该订单已支付");
        }

        OrderPaymentVO vo = jsonObject.toJavaObject(OrderPaymentVO.class);
        vo.setPackageStr(jsonObject.getString("package"));

        return vo;
    }

    /**
     * 支付成功，修改订单状态
     *
     * @param outTradeNo
     */
    public void paySuccess(String outTradeNo) {

        // 根据订单号查询订单
        Orders ordersDB = orderMapper.getByNumber(outTradeNo);

        // 根据订单id更新订单的状态、支付方式、支付状态、结账时间
        Orders orders = Orders.builder()
                .id(ordersDB.getId())
                .status(Orders.TO_BE_CONFIRMED)
                .payStatus(Orders.PAID)
                .checkoutTime(LocalDateTime.now())
                .build();

        orderMapper.update(orders);

        HashMap hashMap = new HashMap();
        hashMap.put("type",1);
        hashMap.put("orderId",ordersDB.getId());
        hashMap.put("content","订单号：" + outTradeNo);
        String json = JSON.toJSONString(hashMap);

        webSocketServer.sendToAllClient(json);
    }


    /**
     * 查询历史订单
     * @param page
     * @param pageSize
     * @param status
     * @return
     */
    @Override
    public PageResult historyOrder(Integer page, Integer pageSize, Integer status) {

        PageHelper.startPage(page,pageSize);

        //查询订单表的数据
        OrdersPageQueryDTO ordersPageQueryDTO = new OrdersPageQueryDTO();
        ordersPageQueryDTO.setStatus(status);
        ordersPageQueryDTO.setUserId(BaseContext.getCurrentId());
        Page<Orders> ordersList = orderMapper.pageQueryHistoryOrder(ordersPageQueryDTO);

        ArrayList<OrderVO> orderVOList = new ArrayList<>();

        if (ordersList != null && ordersList.size() > 0) {
            ordersList.forEach(orders -> {
                OrderVO orderVO = new OrderVO();
                BeanUtils.copyProperties(orders, orderVO);
                List<OrderDetail> orderDetailList = orderDetailMapper.getByOrderNumber(orders.getId());
                orderVO.setOrderDetailList(orderDetailList);
                orderVOList.add(orderVO);
            });
        }
        PageResult pageResult = new PageResult();
        pageResult.setRecords(orderVOList);
        pageResult.setTotal(ordersList.getTotal());
        return pageResult;
    }


    /**
     * 查询订单详情
     * @param id
     * @return
     */
    @Override
    public OrderVO getOrderDetail(Integer id) {

        Orders orders = orderMapper.getById(id);

        List<OrderDetail> orderDetailList = orderDetailMapper.getByOrderNumber(Long.valueOf(id));

        //封装OrderVO
        OrderVO orderVO = new OrderVO();
        BeanUtils.copyProperties(orders,orderVO);
        orderVO.setOrderDetailList(orderDetailList);

        return orderVO;
    }


    /**
     * 再来一单
     * @param id
     */
    @Override
    public void repetition(Long id) {

        List<OrderDetail> orderDetailList = orderDetailMapper.getByOrderNumber(id);

        for (OrderDetail orderDetail : orderDetailList) {
            ShoppingCart shoppingCart = new ShoppingCart();

            if(orderDetail.getDishId() != null){
                Dish dish = dishMapper.selectById(orderDetail.getDishId());
                shoppingCart.setDishId(orderDetail.getDishId());
                shoppingCart.setImage(dish.getImage());
                shoppingCart.setDishFlavor(orderDetail.getDishFlavor());
            }else {
                Setmeal setmeal = setmealMapper.getById(orderDetail.getSetmealId());
                shoppingCart.setSetmealId(orderDetail.getSetmealId());
                shoppingCart.setImage(setmeal.getImage());
            }
            shoppingCart.setName(orderDetail.getName());
            shoppingCart.setAmount(orderDetail.getAmount());
            shoppingCart.setUserId(BaseContext.getCurrentId());
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCart.setNumber(orderDetail.getNumber());

            shoppingCartMapper.insert(shoppingCart);
        }
    }


    /**
     * 管理端订单分页查询
     * @param ordersPageQueryDTO
     * @return
     */
    @Override
    public PageResult conditionPageQuerySearch(OrdersPageQueryDTO ordersPageQueryDTO) {
        PageHelper.startPage(ordersPageQueryDTO.getPage(),ordersPageQueryDTO.getPageSize());

        //查询订单表的数据
        Page<Orders> page = orderMapper.pageQueryHistoryOrder(ordersPageQueryDTO);

        List<OrderVO> orderVOList = new ArrayList<>();
        for (Orders orders : page) {
            OrderVO orderVO = new OrderVO();
            BeanUtils.copyProperties(orders, orderVO);

            orderVO.setOrderDishes(getOrderDishesStr(orders));
            orderVOList.add(orderVO);
        }



        PageResult pageResult = new PageResult();
        pageResult.setRecords(orderVOList);
        pageResult.setTotal(page.getTotal());
        return pageResult;
    }


    /**
     * 统计各个状态的订单数量统计
     * @return
     */
    @Override
    public OrderStatisticsVO countStatistics() {

        Integer toBeConfirmedNumber =  orderMapper.countStatistics(Orders.TO_BE_CONFIRMED);
        Integer confirmedNumber = orderMapper.countStatistics(Orders.CONFIRMED);
        Integer deliveryNumber = orderMapper.countStatistics(Orders.DELIVERY_IN_PROGRESS);

        OrderStatisticsVO orderStatisticsVO = new OrderStatisticsVO();

        orderStatisticsVO.setToBeConfirmed(toBeConfirmedNumber);
        orderStatisticsVO.setConfirmed(confirmedNumber);
        orderStatisticsVO.setDeliveryInProgress(deliveryNumber);

        return orderStatisticsVO;
    }

    @Override
    public OrderVO getOrderVOById(Integer id) {

        Orders orders = orderMapper.getById(id);
        List<OrderDetail> orderDetailList = orderDetailMapper.getByOrderNumber(id.longValue());

        String orderDishes = getOrderDishesStr(orders);

        OrderVO orderVO = new OrderVO();
        BeanUtils.copyProperties(orders,orderVO);
        orderVO.setOrderDishes(orderDishes);
        orderVO.setOrderDetailList(orderDetailList);

        return orderVO;

    }


    /**
     * 商家接单
     * @param ordersConfirmDTO
     */
    @Override
    public void acceptOrder(OrdersConfirmDTO ordersConfirmDTO) {

        Orders orders = new Orders();
        orders.setId(ordersConfirmDTO.getId());
        orders.setStatus(Orders.CONFIRMED);

        orderMapper.update(orders);
    }


    /**
     * 商家拒单
     * @param ordersRejectionDTO
     */
    @Override
    public void rejectionOrder(OrdersRejectionDTO ordersRejectionDTO) {

        // 根据id查询订单
        Orders ordersDB = orderMapper.getById(ordersRejectionDTO.getId().intValue());

        // 订单只有存在且状态为2（待接单）才可以拒单
        if (ordersDB == null || !ordersDB.getStatus().equals(Orders.TO_BE_CONFIRMED)) {
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }


//        //支付状态
//        Integer payStatus = ordersDB.getPayStatus();
//        if (payStatus == Orders.PAID) {
//            //用户已支付，需要退款
//            String refund = weChatPayUtil.refund(
//                    ordersDB.getNumber(),
//                    ordersDB.getNumber(),
//                    new BigDecimal(0.01),
//                    new BigDecimal(0.01));
//            log.info("申请退款：{}", refund);
//        }




        Orders orders = new Orders();
        orders.setId(ordersRejectionDTO.getId());
        orders.setStatus(Orders.CANCELLED);
        orders.setRejectionReason(ordersRejectionDTO.getRejectionReason());
        orderMapper.update(orders);

    }


    /**
     * 完成订单
     * @param id
     */
    @Override
    public void completeOrder(Integer id) {

        //需要加一个判断判断订单不为空且status为要改为状态的上一个状态
        Orders orders = new Orders();
        orders.setId(id.longValue());
        orders.setStatus(Orders.COMPLETED);
        orderMapper.update(orders);
    }


    /**
     * 派送订单
     * @param id
     */
    @Override
    public void deliveryOrder(Long id) {


        // 根据id查询订单
        Orders ordersDB = orderMapper.getById(id.intValue());

        // 校验订单是否存在，并且状态为3
        if (ordersDB == null || !ordersDB.getStatus().equals(Orders.CONFIRMED)) {
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }

        Orders orders = new Orders();
        orders.setId(id);
        orders.setStatus(Orders.DELIVERY_IN_PROGRESS);
        orderMapper.update(orders);
    }


    /**
     * 取消订单
     * @param ordersCancelDTO
     */
    @Override
    public void cancelOrder(OrdersCancelDTO ordersCancelDTO) {

        Orders orders = new Orders();
        orders.setId(ordersCancelDTO.getId());
        orders.setStatus(Orders.CANCELLED);
        orders.setCancelReason(ordersCancelDTO.getCancelReason());
        orders.setCancelTime(LocalDateTime.now());
        orderMapper.update(orders);
    }


    /**
     * 用户催单
     * @param id
     */
    @Override
    public void reminder(Long id) {

        Orders orders = orderMapper.getById(id.intValue());
        if (orders == null) {
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }

        Map map = new HashMap();
        map.put("type",2);
        map.put("orderId",orders.getId());
        map.put("content","订单号：" + orders.getNumber());

        String json = JSON.toJSONString(map);

        webSocketServer.sendToAllClient(json);
    }

    /**
     * 根据订单id获取菜品信息字符串
     *
     * @param orders
     * @return
     */
    private String getOrderDishesStr(Orders orders) {
        // 查询订单菜品详情信息（订单中的菜品和数量）
        List<OrderDetail> orderDetailList = orderDetailMapper.getByOrderNumber(orders.getId());

        // 将每一条订单菜品信息拼接为字符串（格式：宫保鸡丁*3；）
        List<String> orderDishList = orderDetailList.stream().map(x -> {
            String orderDish = x.getName() + "*" + x.getNumber() + ";";
            return orderDish;
        }).collect(Collectors.toList());

        // 将该订单对应的所有菜品信息拼接在一起
        return String.join("", orderDishList);
    }
}
