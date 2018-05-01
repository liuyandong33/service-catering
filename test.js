webpackJsonp([2, 0], [function (t, e, i) {
    "use strict";

    function n(t) {
        return t && t.__esModule ? t : {default: t}
    }

    function s() {
        if (!window.isAdLoaded) {
            window.isAdLoaded = !0;
            var t = window.PAGE_SWITCH && window.PAGE_SWITCH.picVersion,
                e = window.PAGE_SWITCH && window.PAGE_SWITCH.migScriptUrl || "//s3a.pstatp.com/toutiao/gallery_img/dist/img.min.js";
            if (!t) {
                var i = new Date;
                t = i.getFullYear() + "" + (i.getMonth() + 1) + i.getDate() + "_01"
            }
            var n = e + "?ver=" + t;
            (0, u.loadScript)(n, function () {
                setTimeout(function () {
                    window.ad$ && window.ad$.put(), window._czc && window._czc.push(["_trackEvent", "ad-script", "index", "load", 1, ""])
                })
            }, function () {
                setTimeout(function () {
                    window._czc && window._czc.push(["_trackEvent", "ad-script", "index", "block", 1, ""])
                }, 1500)
            })
        }
    }

    var a = i(17), o = n(a), r = i(204), l = n(r);
    i(117);
    var u = i(6), c = i(468), d = n(c);
    o.default.use(l.default), new o.default({
        el: "#app",
        template: "<App/>",
        components: {App: d.default}
    }), window.isAdLoaded = !1, (0, u.addWinLoadEvent)(function () {
        setTimeout(function () {
            s()
        }, 1e3)
    }), setTimeout(function () {
        s()
    }, 15e3)
}, , , , , , function (t, e, i) {
    "use strict";

    function n(t) {
        return t && t.__esModule ? t : {default: t}
    }

    function s(t) {
        if (t != +t) return "";
        "object" !== ("undefined" == typeof t ? "undefined" : (0, _.default)(t)) && (t = new Date(1e3 * t));
        var e, i = Math.floor((new Date - t) / 1e3), n = Math.floor(i / 31536e3);
        return n >= 1 ? e = "年" : (n = Math.floor(i / 2592e3), n >= 1 ? e = "月" : (n = Math.floor(i / 86400), n >= 1 ? e = "天" : (n = Math.floor(i / 3600), n >= 1 ? e = "小时" : (n = Math.floor(i / 60), n >= 1 ? e = "分钟" : (n = i, e = "秒"))))), "秒" === e ? "刚刚" : n + e + "前"
    }

    function a(t) {
        if (t != +t) return "";
        var e = Math.pow(10, 9), i = Math.pow(10, 8), n = Math.pow(10, 7), s = Math.pow(10, 5), a = Math.pow(10, 4),
            o = Math.pow(10, 3), r = "", l = void 0;
        return t - e >= 0 ? (r = "亿", l = Math.floor(t / i)) : t - i >= 0 ? (r = "亿", l = (Number(Math.floor(t / n) / 10).toFixed(1) + "").replace(/\.0$/, "")) : t - s > 0 ? (r = "万", l = Math.floor(t / a)) : t - a >= 0 ? (r = "万", l = (Number(Math.floor(t / o) / 10).toFixed(1) + "").replace(/\.0$/, "")) : l = t, l + r
    }

    function o(t) {
        if (t != +t) return "";
        var e = [], i = void 0;
        return t / 3600 >= 1 && (i = Math.floor(t / 3600), t -= 3600 * i, e.push(i)), t / 60 >= 1 ? (i = Math.floor(t / 60), t -= 60 * i, i < 10 && (i = "0" + i), e.push(i)) : e.push("00"), t < 10 && (t = "0" + t), e.push(t), e.join(":")
    }

    function r(t) {
        for (var e = 0, i = 0, n = t; null != n && n != document.body;) e += n.offsetLeft, i += n.offsetTop, n = n.offsetParent;
        return {left: e, top: i}
    }

    function l() {
        return window.innerHeight && window.innerWidth ? {
            winWidth: window.innerWidth,
            winHeight: window.innerHeight
        } : document.documentElement && document.documentElement.clientHeight && document.documentElement.clientWidth ? {
            winWidth: document.documentElement.clientWidth,
            winHeight: document.documentElement.clientHeight
        } : void 0
    }

    function u(t) {
        return t === window ? Math.max(window.pageYOffset || 0, document.documentElement.scrollTop) : t.scrollTop
    }

    function c(t, e) {
        f(t, e) || (t.className = "" === t.className ? e : t.className + " " + e)
    }

    function d(t, e) {
        if (f(t, e)) {
            for (var i = " " + t.className.replace(/[\t\r\n]/g, "") + " "; i.indexOf(" " + e + " ") >= 0;) i = i.replace(" " + e + " ", " ");
            t.className = i.replace(/^\s+|\s+$/g, "")
        }
    }

    function f(t, e) {
        return e = e || "", 0 !== e.replace(/\s/g, "").length && new RegExp(" " + e + " ").test(" " + t.className + " ")
    }

    function h(t, e, i) {
        var n = document.createElement("script");
        n.src = t, n.crossOrigin = "anonymous", n.onload = function () {
            e && e.call()
        }, n.onerror = function () {
            i && i.call()
        }, document.body.appendChild(n)
    }

    function p(t) {
        var e = window.onload;
        "function" != typeof window.onload ? window.onload = t : window.onload = function () {
            e(), t()
        }
    }

    var m = i(39), _ = n(m);
    t.exports = {
        timeAgo: s,
        numFormat: a,
        durationFormat: o,
        elOffset: r,
        getWinSize: l,
        getScrollTop: u,
        addClass: c,
        removeClass: d,
        hasClass: f,
        loadScript: h,
        addWinLoadEvent: p
    }
}, , , , function (t, e, i) {
    "use strict";

    function n(t) {
        return t && t.__esModule ? t : {default: t}
    }

    Object.defineProperty(e, "__esModule", {value: !0});
    var s = i(17), a = n(s);
    e.default = new a.default
}, , , , , function (t, e, i) {
    "use strict";

    function n(t) {
        return t && t.__esModule ? t : {default: t}
    }

    Object.defineProperty(e, "__esModule", {value: !0});
    var s = i(163), a = n(s);
    e.default = a.default
}, , , , , , function (t, e) {
    "use strict";

    function i(t, e, n) {
        this.$children.forEach(function (s) {
            var a = s.$options.name;
            a === t ? s.$emit.apply(s, [e].concat(n)) : i.apply(s, [t, e].concat([n]))
        })
    }

    Object.defineProperty(e, "__esModule", {value: !0}), e.default = {
        methods: {
            dispatch: function (t, e, i) {
                for (var n = this.$parent || this.$root, s = n.$options.name; n && (!s || s !== t);) n = n.$parent, n && (s = n.$options.name);
                n && n.$emit.apply(n, [e].concat(i))
            }, broadcast: function (t, e, n) {
                i.call(this, t, e, n)
            }
        }
    }
}, , , , , function (t, e, i) {
    i(128);
    var n = i(1)(i(82), i(186), null, null);
    t.exports = n.exports
}, function (t, e, i) {
    "use strict";

    function n(t) {
        return t && t.__esModule ? t : {default: t}
    }

    function s(t) {
        t = t || {}, t.errorCb = function () {
            window.location.href = "https://sso.toutiao.com/login/"
        }, a(t)
    }

    function a(t) {
        t = t || {};
        var e = t.successCb || function () {
        }, i = t.errorCb || function () {
        }, n = t.url || "/user/info/";
        (0, r.default)({
            url: n, method: "get", success: function (t) {
                var n = t || {};
                return "error" == n.message ? void i() : void e(n)
            }, error: function () {
                i()
            }
        })
    }

    Object.defineProperty(e, "__esModule", {value: !0});
    var o = i(3), r = n(o);
    e.default = s
}, , , , , , , , , function (t, e, i) {
    i(122);
    var n = i(1)(i(81), i(180), null, null);
    t.exports = n.exports
}, function (t, e, i) {
    "use strict";

    function n(t) {
        return t && t.__esModule ? t : {default: t}
    }

    function s(t) {
        var e, i = 1, n = 0;
        if (t) for (i = 0, e = t.length - 1; e >= 0; e--) n = t.charCodeAt(e), i = (i << 6 & 268435455) + n + (n << 14), n = 266338304 & i, i = 0 != n ? i ^ n >> 21 : i;
        return i
    }

    function a() {
        var t = k;
        if (t) return t;
        var e = new Date - 0, i = window.location.href, n = s(i);
        return t = "" + e + n + Math.random() + Math.random() + Math.random() + Math.random(), t = t.replace(/\./g, "").substring(0, 32), k = t, t
    }

    function o(t, e, i) {
        if (t.addEventListener) return t.addEventListener(e, i, !1), i;
        if (t.attachEvent) {
            var n = function () {
                var e = window.event;
                e.target = e.srcElement, i.call(t, e)
            };
            return t.attachEvent("on" + e, n), n
        }
    }

    function r(t, e) {
        if (!t) return "";
        var i = t.getAttribute(e);
        return i ? i : ""
    }

    function l(t, e, i) {
        t && t.setAttribute(e, i)
    }

    function u(t) {
        var e = t.getBoundingClientRect();
        return e.top + 10 < $.winHeight && e.bottom > 10
    }

    function c() {
        return window.innerHeight && window.innerWidth ? {
            winWidth: window.innerWidth,
            winHeight: window.innerHeight
        } : document.documentElement && document.documentElement.clientHeight && document.documentElement.clientWidth ? {
            winWidth: document.documentElement.clientWidth,
            winHeight: document.documentElement.clientHeight
        } : void 0
    }

    function d(t) {
        var e, i = S[t];
        if (e = {
            ad_qihu_id: t,
            article_genre: "ad",
            single_mode: !0,
            ad_label: "广告",
            source_url: i.curl || "",
            image_url: i.img || "",
            title: i.title || "",
            source: i.src || "",
            behot_time: Math.floor((new Date).getTime() / 1e3)
        }, i.assets && i.assets.length) {
            e.image_list = [];
            for (var n = 0; n < i.assets.length; n++) {
                var s = i.assets[n];
                e.image_list.push({url: s.img, source_url: s.curl})
            }
            e.has_gallery = !0, e.single_mode = !1
        }
        return e
    }

    function f(t) {
        var e = [];
        for (var i in t) e.push(encodeURIComponent(i) + "=" + encodeURIComponent(t[i]));
        return e.join("&")
    }

    function h(t) {
        if (t = t || {}, !t.url) return !1;
        var e = document.getElementsByTagName("head")[0], i = f(t.data), n = document.createElement("script");
        n.setAttribute("async", ""), e.appendChild(n), n.src = t.url + "?" + i
    }

    function p(t) {
        h({
            url: E,
            data: {
                jsonp: "_qihu_jsonpFun_",
                type: 1,
                of: 4,
                newf: 1,
                showid: I,
                ref: "toutiao.com",
                uid: a(),
                scheme: window.location.protocol.slice(0, -1),
                impct: t,
                time: "ts_" + +new Date
            }
        })
    }

    function m() {
        for (var t, e = 0, i = M.length; e < i; e++) t = M[e], u(t) ? 1 != r(t, "ad_show") && (l(t, "ad_show", 1), _(r(t, "ad_qihu_id"), L, "show")) : l(t, "ad_show", 0)
    }

    function _(t, e, i, n) {
        var s, a = S[t];
        if (a) {
            s = "show" == i ? a.imptk : a.clktk;
            for (var o = 0, r = s.length; o < r; o++) window._ad_qihu_img_ = new Image, window._ad_qihu_img_.src = s[o];
            var l = e + "_" + i;
            n && (l += "_" + n), window.ttAnalysis && window.ttAnalysis.send("event", {ev: l})
        }
    }

    function v() {
        return S.length - (x + 1) < 2 && p(5), x + 1 < S.length ? d(++x) : null
    }

    function g(t) {
        var e, i = v(), n = !1;
        if (i && t.length) {
            for (var s = 0, a = t.length; s < a; s++) if (t[s].ad_id) {
                n = !0, t[s] = i;
                break
            }
            n || (e = t.length > 3 ? 3 : t.length, t.splice(e, 0, i))
        }
    }

    function w(t, e) {
        M = t, L = e, m()
    }

    function y(t, e, i, n) {
        _(t, e, i, n)
    }

    var b = i(13), C = n(b), k = 0, x = -1, S = [], M = [], L = "", I = "P5AcFE", P = "http://show.g.mediav.com/s",
        T = "https://show-g.mediav.com/s", E = 0 === window.location.protocol.indexOf("https") ? T : P, $ = c();
    window._qihu_jsonpFun_ = function (t) {
        t && t.ads && t.ads.length && (S = S.concat(t.ads), window.ttAnalysis && window.ttAnalysis.send("event", {
            ev: "feed_qihu_adshow_count",
            ext_id: t.ads.length
        }))
    }, o(window, "scroll", (0, C.default)(function () {
        m()
    }, 200)), t.exports = {insertQihuAd: g, qhSetAds: w, qhSendMsg: y}
}, function (t, e, i) {
    "use strict";

    function n(t) {
        return t && t.__esModule ? t : {default: t}
    }

    Object.defineProperty(e, "__esModule", {value: !0});
    var s = i(164), a = n(s);
    e.default = a.default
}, , , , , , , , , , , , , , , , , , , , , , , , function (t, e, i) {
    "use strict";

    function n(t) {
        return t && t.__esModule ? t : {default: t}
    }

    Object.defineProperty(e, "__esModule", {value: !0});
    var s = i(152), a = n(s);
    e.default = a.default
}, function (t, e, i) {
    "use strict";

    function n(t) {
        return t && t.__esModule ? t : {default: t}
    }

    Object.defineProperty(e, "__esModule", {value: !0});
    var s = i(200), a = n(s), o = i(201), r = n(o), l = i(202), u = n(l), c = i(3), d = n(c), f = i(72), h = n(f),
        p = i(6), m = i(37), _ = i(203), v = function () {
            function t(e) {
                (0, r.default)(this, t), this.list = [], this.lock = !1, this._stickItem = null, this._refreshItem = null, this._initList = null, this.params = {
                    category: "__all__",
                    utm_source: "toutiao",
                    widen: 1,
                    tadrequire: !0
                }, this.params.category = this._category = e.category || "__all__", "video" === this.params.category && (this.params.new_video_channel = 1), this._url = e.url + "?", this._qhAddSupport = e.qhAdSupport, this._sourceFlags = {}, this._frequency = 0, this._initList = this._feedListInit(e.initList || [])
            }

            return (0, u.default)(t, [{
                key: "_feedListInit", value: function (t) {
                    return this._dataPreHandle(t)
                }
            }, {
                key: "getList", value: function () {
                    var t = this.list;
                    return this._refreshItem && (t = [].concat(t.slice(0, this._refreshItem._index), [this._refreshItem], t.slice(this._refreshItem._index))), this._stickItem && (t = this._stickItem.concat(t)), this._initList && (t = this._initList.concat(t)), t
                }
            }, {
                key: "refresh", value: function (t, e) {
                    this._getData("refresh", t, e)
                }
            }, {
                key: "loadMore", value: function (t, e) {
                    this._getData("loadmore", t, e)
                }
            }, {
                key: "unshiftItem", value: function (t, e, i) {
                    t = this._dataPreHandle(t), this.list = t.concat(this.list), e && e(this.getList(), t.length), i && i()
                }
            }, {
                key: "updateTime", value: function () {
                    var t = this.getList();
                    return t.forEach(function (t) {
                        t.time_ago = (0, p.timeAgo)(t.behot_time)
                    }), t
                }
            }, {
                key: "dislikeItem", value: function (t) {
                    var e = -1, i = function (e) {
                        var i = -1;
                        e = e || [];
                        for (var n = 0, s = e.length; n < s; n++) if (e[n].group_id == t) {
                            i = n;
                            break
                        }
                        return i >= 0 && e.splice(i, 1), i
                    };
                    return e = i(this._initList), e === -1 && (e = i(this._stickItem), 0 === e && (this._stickItem = null)), e === -1 && (e = i(this.list), e !== -1 && this._refreshItem && (e >= this._refreshItem._index ? this._refreshItem._index : --this._refreshItem._index)), this.getList()
                }
            }, {
                key: "_getData", value: function (t, e, i) {
                    var n = this;
                    if (!this.lock) {
                        this.lock = !0, this._setParams(t);
                        var s = function () {
                            n.lock = !1, i && i()
                        };
                        (0, d.default)({
                            url: this.url, data: this.params, success: function (i) {
                                var a = i || {}, o = a.data || [], r = a.next && a.next.max_behot_time;
                                "success" === a.message && o.length && (n._qihuAdInsert(o), o = n._dataPreHandle(o), "refresh" === t ? (n._refreshItem = {
                                    refresh_mode: !0,
                                    behot_time: r,
                                    time_ago: (0, p.timeAgo)(r),
                                    _index: o.length
                                }, n.list = o.concat(n.list)) : n.list = n.list.concat(o), e && e(n.getList(), o.length)), s()
                            }, error: function () {
                                s()
                            }
                        })
                    }
                }
            }, {
                key: "_qihuAdInsert", value: function (t) {
                    this._qhAddSupport && (0 !== this._frequency && this._frequency % 2 === 0 || (0, m.insertQihuAd)(t), this.params.tadrequire = !this.params.tadrequire, this._frequency++)
                }
            }, {
                key: "_dataPreHandle", value: function (t) {
                    var e = this;
                    if (!Array.isArray(t) || 0 === t.length) return [];
                    var i = -1;
                    return this._stickItem = null, t[t.length - 1].honey && t.pop(), t.forEach(function (t, n) {
                        e._dataChange(t), t.is_stick && (i = n)
                    }), i >= 0 && (this._stickItem = t.splice(i, 1)), window.ttAnalysis && window.ttAnalysis.send("event", {
                        ev: "article_show_count",
                        ext_id: t.length
                    }), t
                }
            }, {
                key: "_dataChange", value: function (t) {
                    t.time_ago = (0, p.timeAgo)(t.behot_time), t.comments_count = (0, p.numFormat)(t.comments_count), "__all__" === this._category && this._tagHandle(t), this._mediaHandle(t)
                }
            }, {
                key: "_mediaHandle", value: function (t) {
                    if (!t.media_url) {
                        var e = t.source && t.source.replace(/\s*/gi, "");
                        t.source_tag = e ? e.slice(0, 1) : "", void 0 === this._sourceFlags[e] && (this._sourceFlags[e] = Math.floor(6 * Math.random())), t.avatar_style = "avatar-style-" + this._sourceFlags[e]
                    }
                }
            }, {
                key: "_tagHandle", value: function (t) {
                    var e = {
                        "热点": "hot",
                        "视频": "video",
                        "图片": "image",
                        "社会": "society",
                        "汽车": "car",
                        "体育": "sport",
                        "财经": "finance",
                        "科技": "technology",
                        "娱乐": "entertainment"
                    };
                    t.chinese_tag && (t.tag_style = "tag-style-" + (e[t.chinese_tag] || "other"))
                }
            }, {
                key: "_setParams", value: function (t) {
                    var e = (0, h.default)(), i = 0;
                    this.url = this._url, "refresh" === t ? (i = this.list.length > 0 ? this.list[0].behot_time : 0, this.url += "min_behot_time=" + i) : (i = this.list.length > 0 ? this.list[this.list.length - 1].behot_time : 0, this.url += "max_behot_time=" + i);
                    var n = (0, _.sign)(i + "");
                    (0, a.default)(this.params, {as: e.as, cp: e.cp, _signature: n})
                }
            }]), t
        }();
    e.default = v
}, function (t, e, i) {
    "use strict";

    function n(t) {
        return t && t.__esModule ? t : {default: t}
    }

    function s(t) {
        var e, i = new RegExp("(^| )" + t + "=([^;]*)(;|$)");
        return (e = document.cookie.match(i)) ? unescape(e[2]) : null
    }

    function a(t, e, i) {
        if (t.addEventListener) return t.addEventListener(e, i, !1), i;
        if (t.attachEvent) {
            var n = function () {
                var e = window.event;
                e.target = e.srcElement, i.call(t, e)
            };
            return t.attachEvent("on" + e, n), n
        }
    }

    function o(t, e) {
        if (!t) return "";
        var i = t.getAttribute(e);
        return i ? i : ""
    }

    function r(t, e, i) {
        t && t.setAttribute(e, i)
    }

    function l() {
        return window.innerHeight && window.innerWidth ? {
            winWidth: window.innerWidth,
            winHeight: window.innerHeight
        } : document.documentElement && document.documentElement.clientHeight && document.documentElement.clientWidth ? {
            winWidth: document.documentElement.clientWidth,
            winHeight: document.documentElement.clientHeight
        } : void 0
    }

    function u(t) {
        var e = t.getBoundingClientRect();
        return e.top + 16 < y.winHeight && e.bottom > 16
    }

    function c(t) {
        var e = XMLHttpRequest ? new XMLHttpRequest : new ActiveXObject("Microsoft.XMLHTTP"),
            i = (t.type || "get").toUpperCase(), n = t.url, s = t.data;
        if (n) {
            var a = [];
            for (var o in s) a.push(o + "=" + s[o]);
            "GET" === i ? (n = n + "?" + a.join("&") + "&_=" + Math.random(), e.open(i, n, !0), e.send()) : (e.open(i, n, !0), e.setRequestHeader("X-Requested-With", "XMLHttpRequest"), e.setRequestHeader("Content-type", "application/x-www-form-urlencoded"), e.send(a.join("&"))), e.onload = function () {
                (e.status >= 200 && e.status < 300 || 304 == e.status) && t.success && t.success.call(e, e.responseText)
            }
        }
    }

    function d(t) {
        c({
            url: g,
            type: "POST",
            data: {
                value: t.value,
                tag: "embeded_ad",
                label: t.label,
                is_ad_event: "1",
                log_extra: t.extra,
                category: "web",
                utm_source: s("utm_source"),
                csrfmiddlewaretoken: s("csrftoken")
            },
            success: function (t) {
            }
        }), window.ttAnalysis && window.ttAnalysis.send("event", {ev: "feed_ad_" + t.label})
    }

    function f(t) {
        var e = new Image;
        e.src = t
    }

    function h() {
        for (var t, e = 0, i = w.length; e < i; e++) {
            var n = w[e];
            u(n) ? 1 != o(n, "ad_show") && (r(n, "ad_show", 1), t = {
                value: o(n, "ad_id"),
                extra: o(n, "ad_extra"),
                label: "show",
                track: o(n, "ad_track")
            }, t.track && f(t.track), d(t)) : r(n, "ad_show", 0)
        }
    }

    function p(t) {
        w = t, h()
    }

    function m(t) {
        d(t)
    }

    var _ = i(13), v = n(_), g = "/action_log/", w = [], y = l();
    a(window, "scroll", (0, v.default)(function () {
        h()
    }, 150)), a(window, "resize", (0, v.default)(function () {
        y = l()
    }, 150)), t.exports = {ttSetAds: p, ttSendMsg: m}
}, function (t, e, i) {
    "use strict";

    function n(t) {
        return t && t.__esModule ? t : {default: t}
    }

    Object.defineProperty(e, "__esModule", {value: !0});
    var s = i(159), a = n(s);
    e.default = a.default
}, function (t, e, i) {
    "use strict";

    function n(t) {
        return t && t.__esModule ? t : {default: t}
    }

    Object.defineProperty(e, "__esModule", {value: !0});
    var s = i(160), a = n(s);
    e.default = a.default
}, function (t, e, i) {
    "use strict";

    function n(t) {
        return t && t.__esModule ? t : {default: t}
    }

    Object.defineProperty(e, "__esModule", {value: !0});
    var s = i(161), a = n(s);
    e.default = a.default
}, function (t, e, i) {
    "use strict";

    function n(t) {
        return t && t.__esModule ? t : {default: t}
    }

    Object.defineProperty(e, "__esModule", {value: !0});
    var s = i(162), a = n(s);
    e.default = a.default
}, function (t, e, i) {
    "use strict";

    function n(t) {
        return t && t.__esModule ? t : {default: t}
    }

    function s(t) {
        this.fileQueue = [], this.isUploading = !1, this.options = {
            autoUpload: !1,
            filters: [],
            formdata: {},
            headers: {},
            url: "",
            method: "POST",
            responseType: "json",
            withCredentials: !1,
            progressCbk: null,
            completeCbk: null,
            errorCbk: null,
            useChunk: !0,
            chunkSize: 10485760
        }, o.default.extend(this.options, t)
    }

    var a = i(246), o = n(a), r = i(245), l = n(r);
    s.prototype.getAll = function () {
        return this.fileQueue
    }, s.prototype.clearAll = function () {
        this.fileQueue = []
    }, s.prototype.addToQueue = function (t) {
        for (var t = o.default.isFileList(t) ? o.default.toArray(t) : [t], e = 0, i = t.length; e < i; e++) {
            var n = t[e],
                s = new l.default(this, n, {useChunk: this.options.useChunk, chunkSize: this.options.chunkSize});
            if (!this._filePreHandle(s, this.options.filters)) return !1;
            this.fileQueue.push(s)
        }
        return !0
    }, s.prototype.uploadAll = function () {
        if (this.fileQueue.length) {
            var t = this._getNotUploadedItems();
            t.forEach(function (t) {
                t.onPrepareUpload()
            }), t.length && t[0].upload()
        }
    }, s.prototype.uploadItem = function (t) {
        if (!this.isUploading) {
            var e = o.default.isHTML5() ? "_xhrPost" : "_iframePost", t = t || this._getNotUploadedItems()[0];
            t && (this.isUploading = !0, t.onPrepareUpload(), this[e](t))
        }
    }, s.prototype.abortItem = function (t) {
        var e = o.default.isHTML5() ? "_xhr" : "_form";
        t && 2 === t.status && t[e].abort()
    }, s.prototype.uploadContinue = function (t) {
        o.default.isOnline() && this.uploadItem(t)
    }, s.prototype._onCompleteUpload = function (t) {
        if (this.isUploading = !1, t.useChunk && 2 === t.status) return void this._xhrPost(t);
        this.options.completeCbk && this.options.completeCbk(t), 5 === t.status && this.options.errorCbk && this.options.errorCbk(t);
        var e = this._getReadyItems()[0];
        o.default.isObject(e) && e.upload()
    }, s.prototype._onAbortUpload = function (t) {
        t.onAbort()
    }, s.prototype._onSuccessUpload = function (t) {
        t.onSuccess()
    }, s.prototype._onErrorUpload = function (t) {
        t.onError()
    }, s.prototype._filePreHandle = function (t, e) {
        if (!e.length) return !0;
        for (var i = 0, n = e.length; i < n; i++) {
            var s = e[i];
            if (!s.fn.call(this, t)) return s.fail && s.fail.call(this), !1
        }
        return !0
    }, s.prototype._getNotUploadedItems = function () {
        return this.fileQueue.filter(function (t) {
            return 0 === t.status
        })
    }, s.prototype._getReadyItems = function () {
        return this.fileQueue.filter(function (t) {
            return 1 === t.status
        })
    }, s.prototype._parseHeaders = function (t) {
        var e = {};
        return o.default.isObject(t) ? null : (t.split("\n").forEach(function (t) {
            var i = t.indexOf(":");
            if (i > -1) {
                var n = t.slice(0, i).trim(), s = t.slice(i + 1).trim();
                e[n] = e[n] ? e[n] + "," + s : s
            }
        }), e)
    }, s.prototype._xhrPost = function (t) {
        var e = this, i = t._xhr = new XMLHttpRequest, n = new FormData;
        if (t.onBeforeUpload(), !o.default.isEmptyObject(this.options.formData)) for (var s in this.options.formData) n.append(s, this.options.formData[s]);
        if (i.open(this.options.method, this.options.url, !0), i.responseType = this.options.responseType, i.withCredentials = this.options.withCredentials, !o.default.isEmptyObject(this.options.headers)) for (var s in this.options.headers) i.setRequestHeader(s, this.options.headers[s]);
        if (t.useChunk) {
            var a = t.getChunkFile();
            n.append("file", a.file, t.name), i.setRequestHeader("Content-Range", "bytes " + a.start + "-" + (a.end - 1) + "/" + t.size)
        } else n.append("file", t.file, t.name), i.setRequestHeader("Content-Range", "bytes 0-" + (t.size - 1) + "/" + t.size);
        i.upload.onprogress = function (i) {
            var n = i.lengthComputable ? i.loaded : 0, s = i.lengthComputable ? i.total : -1;
            t.onProgress(n, s), e.options.progressCbk && e.options.progressCbk(t)
        }, i.onload = function (n) {
            var s = i.response, a = 200 == i.status ? "Success" : "Error", o = "_on" + a + "Upload";
            "Success" === a && (t.responseItem = s), e[o](t), e._onCompleteUpload(t)
        }, i.onerror = function (i) {
            e._onErrorUpload(t), e._onCompleteUpload(t)
        }, i.onabort = function (i) {
            e._onAbortUpload(t), e._onCompleteUpload(t)
        }, i.send(n)
    }, s.prototype._iframePost = function () {
    }, window.FileUpload = s, t.exports = s
}, function (t, e, i) {
    "use strict";

    function n(t) {
        return t && t.__esModule ? t : {default: t}
    }

    Object.defineProperty(e, "__esModule", {value: !0});
    var s = i(165), a = n(s);
    e.default = a.default
}, function (t, e, i) {
    "use strict";

    function n(t) {
        return t && t.__esModule ? t : {default: t}
    }

    Object.defineProperty(e, "__esModule", {value: !0});
    var s = i(166), a = n(s);
    e.default = a.default
}, function (t, e, i) {
    "use strict";

    function n(t) {
        return t && t.__esModule ? t : {default: t}
    }

    function s() {
        var t = Math.floor((new Date).getTime() / 1e3), e = t.toString(16).toUpperCase(),
            i = (0, o.default)(t).toString().toUpperCase();
        if (8 != e.length) return {as: "479BB4B7254C150", cp: "7E0AC8874BB0985"};
        for (var n = i.slice(0, 5), s = i.slice(-5), a = "", r = 0; r < 5; r++) a += n[r] + e[r];
        for (var l = "", u = 0; u < 5; u++) l += e[u + 3] + s[u];
        return {as: "A1" + a + e.slice(-3), cp: e.slice(0, 3) + l + "E1"}
    }

    var a = i(73), o = n(a);
    t.exports = s
}, function (t, e, i) {
    function n(t) {
        return t && t.__esModule ? t : {default: t}
    }

    var s, a = i(39);
    n(a);
    !function (n) {
        "use strict";

        function a(t, e) {
            var i = (65535 & t) + (65535 & e), n = (t >> 16) + (e >> 16) + (i >> 16);
            return n << 16 | 65535 & i
        }

        function o(t, e) {
            return t << e | t >>> 32 - e
        }

        function r(t, e, i, n, s, r) {
            return a(o(a(a(e, t), a(n, r)), s), i)
        }

        function l(t, e, i, n, s, a, o) {
            return r(e & i | ~e & n, t, e, s, a, o)
        }

        function u(t, e, i, n, s, a, o) {
            return r(e & n | i & ~n, t, e, s, a, o)
        }

        function c(t, e, i, n, s, a, o) {
            return r(e ^ i ^ n, t, e, s, a, o)
        }

        function d(t, e, i, n, s, a, o) {
            return r(i ^ (e | ~n), t, e, s, a, o)
        }

        function f(t, e) {
            t[e >> 5] |= 128 << e % 32, t[(e + 64 >>> 9 << 4) + 14] = e;
            var i, n, s, o, r, f = 1732584193, h = -271733879, p = -1732584194, m = 271733878;
            for (i = 0; i < t.length; i += 16) n = f, s = h, o = p, r = m, f = l(f, h, p, m, t[i], 7, -680876936), m = l(m, f, h, p, t[i + 1], 12, -389564586), p = l(p, m, f, h, t[i + 2], 17, 606105819), h = l(h, p, m, f, t[i + 3], 22, -1044525330), f = l(f, h, p, m, t[i + 4], 7, -176418897), m = l(m, f, h, p, t[i + 5], 12, 1200080426), p = l(p, m, f, h, t[i + 6], 17, -1473231341), h = l(h, p, m, f, t[i + 7], 22, -45705983), f = l(f, h, p, m, t[i + 8], 7, 1770035416), m = l(m, f, h, p, t[i + 9], 12, -1958414417), p = l(p, m, f, h, t[i + 10], 17, -42063), h = l(h, p, m, f, t[i + 11], 22, -1990404162), f = l(f, h, p, m, t[i + 12], 7, 1804603682), m = l(m, f, h, p, t[i + 13], 12, -40341101), p = l(p, m, f, h, t[i + 14], 17, -1502002290), h = l(h, p, m, f, t[i + 15], 22, 1236535329), f = u(f, h, p, m, t[i + 1], 5, -165796510), m = u(m, f, h, p, t[i + 6], 9, -1069501632), p = u(p, m, f, h, t[i + 11], 14, 643717713), h = u(h, p, m, f, t[i], 20, -373897302), f = u(f, h, p, m, t[i + 5], 5, -701558691), m = u(m, f, h, p, t[i + 10], 9, 38016083), p = u(p, m, f, h, t[i + 15], 14, -660478335), h = u(h, p, m, f, t[i + 4], 20, -405537848), f = u(f, h, p, m, t[i + 9], 5, 568446438), m = u(m, f, h, p, t[i + 14], 9, -1019803690), p = u(p, m, f, h, t[i + 3], 14, -187363961), h = u(h, p, m, f, t[i + 8], 20, 1163531501), f = u(f, h, p, m, t[i + 13], 5, -1444681467), m = u(m, f, h, p, t[i + 2], 9, -51403784), p = u(p, m, f, h, t[i + 7], 14, 1735328473), h = u(h, p, m, f, t[i + 12], 20, -1926607734), f = c(f, h, p, m, t[i + 5], 4, -378558), m = c(m, f, h, p, t[i + 8], 11, -2022574463), p = c(p, m, f, h, t[i + 11], 16, 1839030562), h = c(h, p, m, f, t[i + 14], 23, -35309556), f = c(f, h, p, m, t[i + 1], 4, -1530992060), m = c(m, f, h, p, t[i + 4], 11, 1272893353), p = c(p, m, f, h, t[i + 7], 16, -155497632), h = c(h, p, m, f, t[i + 10], 23, -1094730640), f = c(f, h, p, m, t[i + 13], 4, 681279174), m = c(m, f, h, p, t[i], 11, -358537222), p = c(p, m, f, h, t[i + 3], 16, -722521979), h = c(h, p, m, f, t[i + 6], 23, 76029189), f = c(f, h, p, m, t[i + 9], 4, -640364487), m = c(m, f, h, p, t[i + 12], 11, -421815835), p = c(p, m, f, h, t[i + 15], 16, 530742520), h = c(h, p, m, f, t[i + 2], 23, -995338651), f = d(f, h, p, m, t[i], 6, -198630844), m = d(m, f, h, p, t[i + 7], 10, 1126891415), p = d(p, m, f, h, t[i + 14], 15, -1416354905), h = d(h, p, m, f, t[i + 5], 21, -57434055), f = d(f, h, p, m, t[i + 12], 6, 1700485571), m = d(m, f, h, p, t[i + 3], 10, -1894986606), p = d(p, m, f, h, t[i + 10], 15, -1051523), h = d(h, p, m, f, t[i + 1], 21, -2054922799), f = d(f, h, p, m, t[i + 8], 6, 1873313359), m = d(m, f, h, p, t[i + 15], 10, -30611744), p = d(p, m, f, h, t[i + 6], 15, -1560198380), h = d(h, p, m, f, t[i + 13], 21, 1309151649), f = d(f, h, p, m, t[i + 4], 6, -145523070), m = d(m, f, h, p, t[i + 11], 10, -1120210379), p = d(p, m, f, h, t[i + 2], 15, 718787259), h = d(h, p, m, f, t[i + 9], 21, -343485551), f = a(f, n), h = a(h, s), p = a(p, o), m = a(m, r);
            return [f, h, p, m]
        }

        function h(t) {
            var e, i = "";
            for (e = 0; e < 32 * t.length; e += 8) i += String.fromCharCode(t[e >> 5] >>> e % 32 & 255);
            return i
        }

        function p(t) {
            var e, i = [];
            for (i[(t.length >> 2) - 1] = void 0, e = 0; e < i.length; e += 1) i[e] = 0;
            for (e = 0; e < 8 * t.length; e += 8) i[e >> 5] |= (255 & t.charCodeAt(e / 8)) << e % 32;
            return i
        }

        function m(t) {
            return h(f(p(t), 8 * t.length))
        }

        function _(t, e) {
            var i, n, s = p(t), a = [], o = [];
            for (a[15] = o[15] = void 0, s.length > 16 && (s = f(s, 8 * t.length)), i = 0; i < 16; i += 1) a[i] = 909522486 ^ s[i], o[i] = 1549556828 ^ s[i];
            return n = f(a.concat(p(e)), 512 + 8 * e.length), h(f(o.concat(n), 640))
        }

        function v(t) {
            var e, i, n = "0123456789abcdef", s = "";
            for (i = 0; i < t.length; i += 1) e = t.charCodeAt(i), s += n.charAt(e >>> 4 & 15) + n.charAt(15 & e);
            return s
        }

        function g(t) {
            return unescape(encodeURIComponent(t))
        }

        function w(t) {
            return m(g(t))
        }

        function y(t) {
            return v(w(t))
        }

        function b(t, e) {
            return _(g(t), g(e))
        }

        function C(t, e) {
            return v(b(t, e))
        }

        function k(t, e, i) {
            return e ? i ? b(e, t) : C(e, t) : i ? w(t) : y(t)
        }

        s = function () {
            return k
        }.call(e, i, e, t), !(void 0 !== s && (t.exports = s))
    }(void 0)
}, function (t, e, i) {
    "use strict";

    function n(t) {
        return t && t.__esModule ? t : {default: t}
    }

    Object.defineProperty(e, "__esModule", {value: !0});
    var s = i(167), a = n(s);
    e.default = a.default
}, function (t, e, i) {
    "use strict";

    function n(t) {
        return t && t.__esModule ? t : {default: t}
    }

    Object.defineProperty(e, "__esModule", {value: !0});
    var s = i(168), a = n(s);
    e.default = a.default
}, function (t, e, i) {
    "use strict";

    function n(t) {
        return t && t.__esModule ? t : {default: t}
    }

    Object.defineProperty(e, "__esModule", {value: !0});
    var s = i(169), a = n(s);
    e.default = a.default
}, function (t, e, i) {
    "use strict";

    function n(t) {
        return t && t.__esModule ? t : {default: t}
    }

    Object.defineProperty(e, "__esModule", {value: !0});
    var s = i(170), a = n(s);
    e.default = a.default
}, function (t, e, i) {
    "use strict";

    function n(t) {
        return t && t.__esModule ? t : {default: t}
    }

    Object.defineProperty(e, "__esModule", {value: !0});
    var s = i(171), a = n(s);
    e.default = a.default
}, function (t, e, i) {
    "use strict";

    function n(t) {
        return t && t.__esModule ? t : {default: t}
    }

    Object.defineProperty(e, "__esModule", {value: !0});
    var s = i(174), a = n(s);
    e.default = a.default
}, function (t, e, i) {
    "use strict";

    function n(t) {
        return t && t.__esModule ? t : {default: t}
    }

    Object.defineProperty(e, "__esModule", {value: !0});
    var s = i(17), a = n(s), o = i(13), r = n(o), l = i(6);
    e.default = {
        name: "Channel",
        props: {
            header: Object,
            channels: {type: Object, required: !0},
            isSuspended: {type: Boolean, default: !1},
            scrollHandler: {
                type: Function, default: function () {
                    return (0, l.getScrollTop)(window) > 40
                }
            }
        },
        data: function () {
            return {isFixed: !1, throttledScrollHandler: new Function}
        },
        created: function () {
            var t = this;
            this.isFixed = this.isSuspended, this.channels.items.map(function (t) {
                return t.url || (t.url = "javascript:void(0);"), t
            }), this.channels.more && this.channels.more.map(function (t) {
                return t.url || (t.url = "javascript:void(0);"), t
            }), this.throttledScrollHandler = (0, r.default)(function () {
                t.isFixed = t.scrollHandler()
            }, 80), window.addEventListener("scroll", this.throttledScrollHandler, !1)
        },
        mounted: function () {
            var t = this;
            this.channels.more && this.channels.more.forEach(function (e, i) {
                if (e.url === t.channels.tag) {
                    var n = t.channels.items.length, s = t.channels.items[n - 1];
                    a.default.set(t.channels.items, n - 1, e), a.default.set(t.channels.more, i, s)
                }
            })
        },
        destroyed: function () {
            window.removeEventListener("scroll", this.throttledScrollHandler)
        },
        methods: {
            itemClick: function (t) {
                this.$emit("channel-item-click", t)
            }
        }
    }
}, function (t, e, i) {
    "use strict";

    function n(t) {
        return t && t.__esModule ? t : {default: t}
    }

    Object.defineProperty(e, "__esModule", {value: !0});
    var s = i(28), a = n(s), o = i(3), r = n(o), l = i(21), u = n(l), c = i(27), d = n(c);
    e.default = {
        name: "Dislike",
        mixins: [u.default],
        props: {
            group_id: {type: String, default: ""},
            item_id: {type: String, default: ""},
            ad_id: {type: String, default: ""},
            getUserInfoUrl: {type: String, default: "/user/info/"},
            dispatchTarget: {type: String, default: "FeedBox"}
        },
        components: {},
        methods: {
            dislikeClick: function () {
                var t = this;
                (0, d.default)({
                    successCb: function () {
                        t._disLike()
                    }, url: this.getUserInfoUrl
                })
            }, _disLike: function () {
                var t = this, e = this._getParams();
                (0, r.default)({
                    url: "/user_data/batch_action/?aid=24",
                    method: "post",
                    headers: {"Content-Type": "application/json; charset=utf-8"},
                    data: e,
                    success: function (e) {
                        var i = e || {}, n = "不好意思，网络错误";
                        "success" == i.message && (t.dispatch(t.dispatchTarget, "feed-item-dislike", t.group_id), n = "将减少推荐类似内容"), t.$Toast({
                            message: n,
                            position: "middle"
                        })
                    }
                })
            }, _getParams: function () {
                var t = {
                    actions: [{
                        action: "dislike",
                        aggr_type: 1,
                        id: this.group_id,
                        item_id: this.item_id,
                        ad_extra: {},
                        type: 1,
                        timestamp: Math.floor(+new Date / 1e3)
                    }]
                };
                if (this.ad_id) for (var e = document.querySelectorAll(".feed-infinite-wrapper .J_ad") || [], i = 0, n = e.length; i < n; i++) {
                    var s = e[i], o = s.getAttribute("ad_id");
                    if (o === this.ad_id) {
                        t.actions[0].type = 3, t.actions[0].ad_id = o, t.actions[0].ad_extra.log_extra = s.getAttribute("ad_extra");
                        break
                    }
                }
                return t = (0, a.default)(t)
            }
        }
    }
}, function (t, e, i) {
    "use strict";

    function n(t) {
        return t && t.__esModule ? t : {default: t}
    }

    Object.defineProperty(e, "__esModule", {value: !0});
    var s = i(36), a = n(s);
    e.default = {
        props: {
            item: {type: Object, default: {}},
            dislikeUrl: {type: String, default: "/api/dislike/"},
            getUserInfoUrl: {type: String, default: "/user/info/"}
        }, computed: {}, components: {Dislike: a.default}
    }
}, function (t, e, i) {
    "use strict";

    function n(t) {
        return t && t.__esModule ? t : {default: t}
    }

    Object.defineProperty(e, "__esModule", {value: !0});
    var s = i(13), a = n(s), o = i(6), r = i(21), l = n(r);
    e.default = {
        name: "MsgAlert",
        mixins: [l.default],
        props: {category: String, suspensionTip: {type: Boolean, default: !0}},
        data: function () {
            return {msgHidden: !0, msgShow: !1, msgFixed: !1, articleCount: -1}
        },
        mounted: function () {
            var t = this;
            this.waitTime = 36e4, this.$on("feed-refresh-count", function (t) {
                var e = this;
                this.msgTimer && clearTimeout(this.msgTimer), this.articleCount = t, this.msgHidden = !1, this.msgTimer = setTimeout(function () {
                    e.msgHidden = !0
                }, 2300)
            }), this.$on("feed-refresh", function () {
                this.msgShow = !1
            }), this.suspensionTip && (this.msgOffsetTop = 0, window.addEventListener("scroll", (0, a.default)(function () {
                t._stateChange()
            }, 50), !1), this._timerUpdate())
        },
        methods: {
            closeMsgClick: function () {
                this.msgShow = !1, this._timerUpdate()
            }, feedRefreshClick: function () {
                this.msgShow = !1, this._timerUpdate(), this.dispatch("FeedBox", "feed-refresh")
            }, _timerUpdate: function () {
                var t = this;
                this.refreshTimer && (clearTimeout(this.refreshTimer), this.refreshTimer = null), this.refreshTimer = setTimeout(function () {
                    t.msgShow = !0, t.msgFixed = !1, t.$nextTick(function () {
                        t._stateChange()
                    })
                }, this.waitTime)
            }, _stateChange: function () {
                if (this.msgShow) {
                    var t = (0, o.elOffset)(this.$refs.msgAlertPlace).top, e = (0, o.getScrollTop)(window);
                    this.msgOffsetTop < t && (this.msgOffsetTop = t), this.msgFixed = e > this.msgOffsetTop
                }
            }
        }
    }
}, function (t, e, i) {
    "use strict";

    function n(t) {
        return t && t.__esModule ? t : {default: t}
    }

    Object.defineProperty(e, "__esModule", {value: !0});
    var s = i(26), a = n(s);
    e.default = {props: {item: {type: Object, default: {}}}, components: {FooterBar: a.default}}
}, function (t, e, i) {
    "use strict";

    function n(t) {
        return t && t.__esModule ? t : {default: t}
    }

    Object.defineProperty(e, "__esModule", {value: !0});
    var s = i(26), a = n(s);
    e.default = {props: {item: {type: Object, default: {}}}, components: {FooterBar: a.default}}
}, function (t, e) {
    "use strict";
    Object.defineProperty(e, "__esModule", {value: !0}), e.default = {
        props: {item: {type: Object, default: {}}},
        methods: {}
    }
}, function (t, e, i) {
    "use strict";

    function n(t) {
        return t && t.__esModule ? t : {default: t}
    }

    Object.defineProperty(e, "__esModule", {value: !0});
    var s = i(26), a = n(s);
    e.default = {
        props: {
            item: {type: Object, default: {}},
            dislikeUrl: {type: String, default: "/api/dislike/"},
            getUserInfoUrl: {type: String, default: "/user/info/"}
        }, components: {FooterBar: a.default}
    }
}, function (t, e, i) {
    "use strict";

    function n(t) {
        return t && t.__esModule ? t : {default: t}
    }

    Object.defineProperty(e, "__esModule", {value: !0});
    var s = i(36), a = n(s), o = i(6);
    e.default = {
        props: {item: {type: Object, default: {}}}, computed: {
            styles: function () {
                var t = {};
                return this.item.ugc_data.ugc_images.length && (t.height = "179px"), t
            }
        }, components: {Dislike: a.default}, filters: {
            formatCount: function (t) {
                return t ? t = (0, o.numFormat)(t) : "0"
            }
        }
    }
}, function (t, e, i) {
    "use strict";

    function n(t) {
        return t && t.__esModule ? t : {default: t}
    }

    Object.defineProperty(e, "__esModule", {value: !0});
    var s = i(3), a = n(s), o = i(25), r = n(o), l = {
        success: "已提交,感谢您的意见",
        fail: "提交错误,请稍后重试",
        mail_error: "请输入正确的联系方式",
        content_error: "请输入您的意见",
        content_length_error: "意见长度超出限制"
    };
    e.default = {
        name: "Feedback", props: {show: {type: Boolean, default: !1}}, data: function () {
            return {feedbackDialogVisible: !1, feedbackEmail: "", feedbackContent: "", isSubmitDisabled: !1}
        }, methods: {
            submit: function () {
                var t = this, e = t.feedbackEmail, i = t.feedbackContent;
                return e.length < 5 ? (t.$Toast(l.mail_error), void document.querySelector(".feedback .email").focus()) : "" === i.trim() ? (t.$Toast(l.content_error), void document.querySelector(".feedback .text").focus()) : (t.isSubmitDisabled = !0, void(0, a.default)({
                    headers: {"X-CSRFToken": r.default.get("csrftoken")},
                    url: "/post_message/",
                    method: "post",
                    data: {appkey: "web", uuid: e, content: "[" + window.location.host + "]" + i},
                    success: function (e) {
                        e && "success" === e.message ? (t.feedbackEmail = "", t.feedbackContent = "", t.feedbackDialogVisible = !1) : t.$Toast(l.fail)
                    },
                    error: function () {
                        t.$Toast(l.fail)
                    },
                    complete: function () {
                        t.isSubmitDisabled = !1
                    }
                }))
            }
        }, created: function () {
            this.feedbackDialogVisible = this.show
        }
    }
}, function (t, e, i) {
    "use strict";

    function n(t) {
        return t && t.__esModule ? t : {default: t}
    }

    Object.defineProperty(e, "__esModule", {value: !0});
    var s = i(71), a = n(s), o = i(38), r = n(o), l = i(65), u = n(l);
    e.default = {
        name: "TtHeader", data: function () {
            return {
                isShowFeedback: !1,
                logoImg: i(143),
                navItem: [{name: "推荐", url: "/", en: "recommend"}, {
                    name: "热点",
                    url: "/ch/news_hot/",
                    en: "hot"
                }, {name: "视频", url: "/ch/video/", en: "video"}, {
                    name: "图片",
                    url: "/ch/news_image/",
                    en: "image"
                }, {name: "段子", url: "/ch/essay_joke/", en: "essay"}, {
                    name: "社会",
                    url: "/ch/news_society/",
                    en: "society"
                }, {name: "娱乐", url: "/ch/news_entertainment/", en: "entertainment"}, {
                    name: "科技",
                    url: "/ch/news_tech/",
                    en: "tech"
                }, {name: "汽车", url: "/ch/news_car/", en: "car"}, {
                    name: "体育",
                    url: "/ch/news_sports/",
                    en: "sports"
                }, {name: "财经", url: "/ch/news_finance/", en: "finance"}, {
                    name: "军事",
                    url: "/ch/news_military/",
                    en: "military"
                }, {name: "国际", url: "/ch/news_world/", en: "world"}, {
                    name: "时尚",
                    url: "/ch/news_fashion/",
                    en: "fashion"
                }, {name: "旅游", url: "/ch/news_travel/", en: "travel"}],
                navMore: [{name: "探索", url: "/ch/news_discovery/", en: "discovery"}, {
                    name: "育儿",
                    url: "/ch/news_baby/",
                    en: "baby"
                }, {name: "养生", url: "/ch/news_regimen/", en: "regimen"}, {
                    name: "美文",
                    url: "/ch/news_essay/",
                    en: "essay"
                }, {name: "游戏", url: "/ch/news_game/", en: "game"}, {
                    name: "历史",
                    url: "/ch/news_history/",
                    en: "history"
                }, {name: "美食", url: "/ch/news_food/", en: "food"}]
            }
        }, props: {
            showUser: {type: Boolean, default: !0}, options: {
                type: Object, default: function () {
                    return {}
                }
            }, middlebarWidth: {type: Number, default: 1120}
        }, methods: {
            closeFeedback: function () {
                this.isShowFeedback = !1
            }, showFeedback: function () {
                this.isShowFeedback = !0
            }, jumpToLogin: function () {
                setTimeout(function () {
                    location.href = "https://sso.toutiao.com/login/"
                }, 250)
            }
        }, components: {WeatherBox: a.default, SearchBox: r.default, Feedback: u.default}
    }
}, function (t, e, i) {
    "use strict";

    function n(t) {
        return t && t.__esModule ? t : {default: t}
    }

    Object.defineProperty(e, "__esModule", {value: !0});
    var s = i(3), a = n(s), o = i(15), r = n(o);
    e.default = {
        name: "HotImages",
        data: function () {
            return {list: []}
        },
        mounted: function () {
            var t = this;
            (0, a.default)({
                url: this.getHotImagesUrl, data: {widen: 1}, method: "get", success: function (e) {
                    e.data && (t.list = e.data)
                }
            })
        },
        props: {
            count: {type: Number, default: 4},
            title: {type: String, default: "精彩图片"},
            getHotImagesUrl: {type: String, default: "/api/pc/hot_gallery/"}
        },
        computed: {
            listCount: function () {
                var t = this.list.slice(0, this.count);
                return t.length > 2 && t.splice(2, 0, {}), t.length > 6 && t.splice(6, 0, {}), t
            }
        },
        components: {Pane: r.default}
    }
}, function (t, e, i) {
    "use strict";

    function n(t) {
        return t && t.__esModule ? t : {default: t}
    }

    Object.defineProperty(e, "__esModule", {value: !0});
    var s = i(3), a = n(s), o = i(6), r = i(15), l = n(r);
    e.default = {
        name: "HotVideos",
        data: function () {
            return {list: []}
        },
        mounted: function () {
            var t = this;
            (0, a.default)({
                url: this.getHotVideosUrl, data: {widen: 1}, method: "get", success: function (e) {
                    e.data && (t.list = e.data)
                }
            })
        },
        props: {
            count: {type: Number, default: 4},
            title: {type: String, default: "热门视频"},
            getHotVideosUrl: {type: String, default: "/api/pc/hot_video/"}
        },
        computed: {
            listCount: function () {
                var t = this.list.slice(0, this.count);
                return t.length > 2 && t.splice(3, 0, {}), t
            }
        },
        filters: {
            formatCount: function (t) {
                return t ? t = (0, o.numFormat)(t) : "0"
            }
        },
        components: {Pane: l.default}
    }
}, function (t, e) {
    "use strict";
    Object.defineProperty(e, "__esModule", {value: !0}), e.default = {name: "Pane", props: {title: {type: String}}}
}, function (t, e, i) {
    "use strict";

    function n(t) {
        return t && t.__esModule ? t : {default: t}
    }

    Object.defineProperty(e, "__esModule", {value: !0});
    var s = i(3), a = n(s), o = i(17), r = n(o);
    r.default.component("sortable-list", {
        functional: !0, render: function (t, e) {
            var i = e.props.item, n = e.props.index + 1;
            return t("li", e.data, [t("i", {attrs: {class: "search-no search-no-" + n}}, [n]), t("span", {attrs: {class: "search-text"}}, [i.value])])
        }, props: {item: {type: Object, required: !0}, index: Number}
    }), e.default = {
        name: "SearchBox",
        props: {
            searchUrl: {type: String, default: "//www.toutiao.com/search/?keyword="},
            getSearchSuggestionUrl: {type: String, default: "/search/sug/"},
            getHotSearchUrl: {type: String, default: "/search/suggest/initial_page/"}
        },
        data: function () {
            return {
                wordslist: [],
                searchWord: "",
                timeout: null,
                placeHolder: "请输入要搜索的内容",
                resultType: "sortable-list",
                presetWord: ""
            }
        },
        methods: {
            querySearchAsync: function (t, e) {
                var i = [];
                t ? (this.resultType = "", (0, a.default)({
                    url: this.getSearchSuggestionUrl,
                    data: {keyword: t},
                    method: "GET",
                    success: function (t) {
                        var n = t && t.data ? t.data : [];
                        n.forEach(function (t) {
                            i.push({value: t})
                        }), e(i)
                    }
                })) : (this.resultType = "sortable-list", i = this.wordslist, e(i))
            }, toSearchPage: function (t) {
                t && window.open(this.searchUrl + t)
            }, handleIconClick: function () {
                this.toSearchPage(this.searchWord)
            }, handleSelect: function (t) {
                this.toSearchPage(t.value), t.index && (window._czc && _czc.push(["_trackEvent", "search", "hotword" + t.index, "click"]), window.ttAnalysis && window.ttAnalysis.send("event", {ev: "search_hotword" + t.index + "_click"}))
            }, handleEnterKeydown: function () {
                this.toSearchPage(this.searchWord)
            }, handleFocus: function () {
                this.placeHolder = ""
            }, handleBlur: function () {
                this.placeHolder = this.presetWord
            }
        },
        mounted: function () {
            var t = this;
            (0, a.default)({
                url: this.getHotSearchUrl, method: "GET", success: function (e) {
                    if ("success" === e.message) {
                        if (!Array.isArray(e.data) || 0 === e.data.length) return;
                        var i = [];
                        e.data.forEach(function (t, e) {
                            i.push({value: t, index: e + 1})
                        }), t.wordslist = i, t.presetWord = "大家都在搜：" + i[0].value, t.placeHolder = t.presetWord
                    }
                }
            })
        }
    }
}, function (t, e, i) {
    "use strict";

    function n(t) {
        return t && t.__esModule ? t : {default: t}
    }

    Object.defineProperty(e, "__esModule", {value: !0});
    var s = i(6), a = i(13), o = n(a);
    e.default = {
        name: "Toolbar",
        props: {
            showHome: {type: Boolean, default: !1},
            showReport: {type: Boolean, default: !1},
            showRefresh: {type: Boolean, default: !1},
            showTop: {type: Boolean, default: !0},
            refreshMethod: Function
        },
        data: function () {
            return {hasScrolled: !1}
        },
        computed: {
            reallyShowTop: function () {
                return this.showTop && this.hasScrolled
            }
        },
        mounted: function () {
            var t = this, e = 0;
            window.addEventListener("scroll", (0, o.default)(function () {
                e = (0, s.getScrollTop)(window), e > 100 ? t.hasScrolled = !0 : t.hasScrolled = !1
            }, 500), !1)
        },
        methods: {
            scrollToTop: function () {
                window.scrollTo(0, 0)
            }, refresh: function () {
                this.refreshMethod && this.refreshMethod()
            }
        }
    }
}, function (t, e, i) {
    "use strict";

    function n(t) {
        return t && t.__esModule ? t : {default: t}
    }

    function s(t) {
        return t >= 0 && t <= 100 ? "#5cbf4c" : t >= 101 && t <= 200 ? "#ff9f2d" : t >= 201 ? "#ff5f5f" : "rgba(214, 117, 3, 0.8)"
    }

    Object.defineProperty(e, "__esModule", {value: !0});
    var a = i(28), o = n(a), r = i(3), l = n(r), u = i(25), c = n(u);
    e.default = {
        name: "WeatherBox",
        props: {
            weatherCityUrl: {type: String, default: "/stream/widget/local_weather/city/"},
            weatherDataUrl: {type: String, default: "/stream/widget/local_weather/data/"}
        },
        data: function () {
            return {
                provinces: [],
                citys: [],
                rs: null,
                province: "北京",
                city: "北京",
                weathercity: "",
                weather: {},
                iconClass: {},
                showWeather: !0,
                showPopup: !1,
                selecting: !1,
                aqiColor: ""
            }
        },
        mounted: function () {
            var t = this;
            if (window.localStorage) {
                var e = localStorage.getItem("weather_city");
                e && (e = JSON.parse(e), this.city = e.city, this.province = e.province)
            }
            this.onSubmitClick(), (0, l.default)({
                url: this.weatherCityUrl, method: "get", success: function (i) {
                    var n = !(!i || !i.data) && i.data, s = [], a = [];
                    if (n) {
                        for (var o in n) {
                            s.push({value: o, label: o}), a = n[o], n[o] = [];
                            for (var r in a) n[o].push({value: r, label: r})
                        }
                        t.provinces = s, t.rs = n, e && (t.citys = t.rs[e.province])
                    }
                }
            })
        },
        methods: {
            changeLocation: function () {
                this.showWeather = !1
            }, isSelecting: function (t) {
                this.selecting = t
            }, onSubmitClick: function () {
                var t = this;
                (0, l.default)({
                    url: this.weatherDataUrl, data: {city: t.city}, method: "get", success: function (e) {
                        e.data && e.data.weather && (t.weathercity = e.data.city, t.weather = e.data.weather, t.aqiColor = s(t.weather.aqi))
                    }
                }), window.localStorage && (localStorage.setItem("weather_city", (0, o.default)({
                    city: this.city,
                    province: this.province
                })), c.default.set("WEATHER_CITY", this.city, {expires: 100})), t.showWeather = !0
            }, onCancelClick: function () {
                this.showWeather = !0
            }, handleProvinceChange: function (t) {
                this.rs && (this.citys = this.rs[t], this.city = this.citys[0].value)
            }, handleMouseEnter: function (t) {
                this.showPopup = !0
            }, handleMouseLeave: function (t) {
                return !this.selecting && void(this.showPopup = !1)
            }
        },
        watch: {
            weather: function (t) {
                this.iconClass = {
                    today: "weather-icon-" + t.weather_icon_id,
                    tom: "weather-icon-" + t.tomorrow_weather_icon_id,
                    dat: "weather-icon-" + t.dat_weather_icon_id
                }
            }
        }
    }
}, function (t, e, i) {
    "use strict";

    function n(t) {
        return t && t.__esModule ? t : {default: t}
    }

    Object.defineProperty(e, "__esModule", {value: !0});
    var s = i(62), a = n(s);
    e.default = {
        props: {tag: {type: String, default: ""}, local: {type: String, default: ""}}, data: function () {
            return {
                header: {logoImg: i(145), logoAlt: "今日头条", logoWidth: "108px", logoHeight: "27px"},
                channels: {
                    tag: this.tag,
                    items: [{name: "推荐", url: "/", log: "recommand"}, {
                        name: "热点",
                        url: "/ch/news_hot/",
                        log: "hot"
                    }, {name: "图片", url: "/ch/news_image/", log: "image", target: "_blank"}, {
                        name: "科技",
                        url: "/ch/news_tech/",
                        log: "technology"
                    }, {name: "娱乐", url: "/ch/news_entertainment/", log: "entertainment"}, {
                        name: "游戏",
                        url: "/ch/news_game/",
                        log: "game"
                    }, {name: "体育", url: "/ch/news_sports/", log: "sports"}, {
                        name: "汽车",
                        url: "/ch/news_car/",
                        log: "car"
                    }, {name: "财经", url: "/ch/news_finance/", log: "finance"}, {
                        name: "搞笑",
                        url: "/ch/funny/",
                        log: "funny"
                    }],
                    more: [{name: "军事", url: "/ch/news_military/", log: "military"}, {
                        name: "国际",
                        url: "/ch/news_world/",
                        log: "international"
                    }, {name: "时尚", url: "/ch/news_fashion/", log: "fashion"}, {
                        name: "旅游",
                        url: "/ch/news_travel/",
                        log: "travel"
                    }, {name: "探索", url: "/ch/news_discovery/", log: "explore"}, {
                        name: "育儿",
                        url: "/ch/news_baby/",
                        log: "childcare"
                    }, {name: "养生", url: "/ch/news_regimen/", log: "health"}, {
                        name: "美文",
                        url: "/ch/news_essay/",
                        log: "article"
                    }, {name: "历史", url: "/ch/news_history/", log: "history"}, {
                        name: "美食",
                        url: "/ch/news_food/",
                        log: "food"
                    }]
                }
            }
        }, components: {Channel: a.default}, created: function () {
        }
    }
}, function (t, e, i) {
    "use strict";
    Object.defineProperty(e, "__esModule", {value: !0}), e.default = {
        name: "Company", data: function () {
            return {gonganImg: i(144)}
        }, computed: {
            year: function () {
                var t = new Date;
                return t.getFullYear()
            }
        }
    }
}, function (t, e, i) {
    "use strict";

    function n(t) {
        return t && t.__esModule ? t : {default: t}
    }

    Object.defineProperty(e, "__esModule", {value: !0});
    var s = i(15), a = n(s);
    e.default = {
        name: "FriendLinks", data: function () {
            return {
                friendLink: [{url: "http://www.gmw.cn/", name: "光明网"}, {
                    url: "http://www.cnr.cn/",
                    name: "央广网"
                }, {url: "http://www.cri.cn", name: "国际在线"}, {
                    url: "http://www.tibet.cn/",
                    name: "中国西藏网"
                }, {url: "http://www.cankaoxiaoxi.com/", name: "参考消息"}, {
                    url: "http://www.huanqiu.com/",
                    name: "环球网"
                }, {url: "http://www.cyol.com", name: "中青在线"}, {
                    url: "http://www.youth.cn/",
                    name: "中青网"
                }, {url: "http://www.haiwainet.cn/", name: "海外网"}, {
                    url: "http://h5.china.com.cn",
                    name: "中国网"
                }, {url: "http://www.k618.cn/", name: "未来网"}, {
                    url: "http://qianlong.com/",
                    name: "千龙网"
                }, {url: "http://www.bjnews.com.cn/", name: "新京报"}, {
                    url: "http://www.ynet.com/",
                    name: "北青网"
                }, {url: "http://www.fawan.com/", name: "法制晚报"}, {
                    url: "http://www.morningpost.com.cn",
                    name: "北京晨报"
                }, {url: "http://www.bbtnews.com.cn/", name: "北京商报"}, {
                    url: "http://www.stardaily.com.cn/",
                    name: "北京娱乐信报"
                }, {url: "http://www.oeeee.com/", name: "奥一网"}, {
                    url: "http://www.ycwb.com/",
                    name: "金羊网"
                }, {url: "http://www.hsw.cn/", name: "华商网"}, {
                    url: "http://www.xinmin.cn",
                    name: "新民网"
                }, {url: "http://www.rednet.cn/index.html", name: "红网"}, {
                    url: "http://www.jschina.com.cn",
                    name: "中国江苏网"
                }, {url: "http://www.jxnews.com.cn/", name: "中国江西网"}, {
                    url: "http://www.iqilu.com/",
                    name: "齐鲁网"
                }, {url: "http://www.hinews.cn/news/", name: "南海网"}, {
                    url: "http://www.ahwang.cn/",
                    name: "安徽网"
                }, {url: "http://www.hebnews.cn/", name: "河北新闻网"}, {
                    url: "http://www.mnw.cn/",
                    name: "闽南网"
                }, {url: "http://www.hxnews.com/", name: "海峡网"}, {
                    url: "http://www.voc.com.cn/",
                    name: "华声在线"
                }, {url: "http://tv.cztv.com", name: "中国蓝TV"}, {
                    url: "http://www.lnd.com.cn/",
                    name: "北国网"
                }, {url: "http://www.longhoo.net/", name: "龙虎网"}, {
                    url: "http://www.timedg.com/",
                    name: "东莞时间网"
                }, {url: "http://www.autohome.com.cn/", name: "汽车之家"}, {
                    url: "http://www.onlylady.com/",
                    name: "Onlylady女人志"
                }, {url: "http://123.chinaso.com/", name: "中国搜索"}, {
                    url: "http://www.nbd.com.cn/",
                    name: "每日经济新闻"
                }, {url: "http://www.cheshi.com/", name: "网上车市"}, {
                    url: "http://www.news18a.com/",
                    name: "网通社汽车"
                }, {url: "http://www.enorth.com.cn/", name: "北方网"}, {
                    url: "http://www.hnt.gov.cn/",
                    name: "湖南省旅发委官网"
                }, {url: "http://www.leju.com/", name: "乐居网"}]
            }
        }, props: {title: String}, components: {PaneModule: a.default}
    }
}, function (t, e, i) {
    "use strict";

    function n(t) {
        return t && t.__esModule ? t : {default: t}
    }

    Object.defineProperty(e, "__esModule", {value: !0});
    var s = i(15), a = n(s), o = i(3), r = n(o);
    e.default = {
        name: "HotNews",
        props: {list: Array, count: {type: Number, default: 4}, title: String},
        data: function () {
            return {resList: []}
        },
        mounted: function () {
            this.list ? this.resList = this.list.slice(0, this.count) : this._getData()
        },
        methods: {
            _getData: function () {
                var t = this;
                (0, r.default)({
                    url: "/api/pc/realtime_news/", method: "get", success: function (e) {
                        if ("success" === e.message) {
                            var i = e.data || [];
                            t.resList = i.slice(0, t.count)
                        }
                    }, error: function () {
                    }
                })
            }
        },
        components: {PaneModule: a.default}
    }
}, function (t, e, i) {
    "use strict";

    function n(t) {
        return t && t.__esModule ? t : {default: t}
    }

    Object.defineProperty(e, "__esModule", {value: !0});
    var s = i(15), a = n(s);
    e.default = {
        name: "MoreLinks", data: function () {
            return {
                moreLinks: [{url: "/about/", title: "关于头条"}, {url: "/join/", title: "加入头条"}, {
                    url: "/report/",
                    title: "媒体报道"
                }, {url: "/media_partners/", title: "媒体合作"}, {
                    url: "/cooperation/",
                    title: "产品合作"
                }, {
                    url: "/media_cooperation/",
                    title: "合作说明"
                }, {
                    url: "https://ad.toutiao.com/promotion/?source2=pchomemore",
                    title: "广告投放",
                    target: "_blank"
                }, {url: "/contact/", title: "联系我们"}, {
                    url: "/user_agreement/",
                    title: "用户协议"
                }, {url: "/privacy_protection/", title: "隐私政策"}, {
                    url: "/complain/",
                    title: "侵权投诉"
                }, {
                    url: "/corrupt_report/",
                    title: "廉洁举报"
                }, {
                    url: "http://renzheng.toutiao.com/guide?platform=%27PC%27&source=%27www.toutiao.com%27",
                    title: "企业认证"
                }]
            }
        }, props: {title: String}, components: {PaneModule: a.default}
    }
}, function (t, e, i) {
    "use strict";

    function n(t) {
        return t && t.__esModule ? t : {default: t}
    }

    Object.defineProperty(e, "__esModule", {value: !0});
    var s = i(10), a = n(s);
    e.default = {
        name: "SlideList",
        props: {
            slideList: {type: Array, default: []},
            autoplay: {type: Boolean, default: !1},
            autoplaySpeed: {type: Number, default: 3e3}
        },
        data: function () {
            return {currentIndex: 0}
        },
        mounted: function () {
            var t = this;
            this.setAutoplay(), a.default.$on("slide-tab-change", function (e) {
                t.currentIndex !== e && (t.currentIndex = e, t.setAutoplay())
            })
        },
        methods: {
            add: function (t) {
                var e = this.currentIndex;
                for (e += t; e < 0;) e += this.slideList.length;
                e %= this.slideList.length, this.currentIndex = e, a.default.$emit("slide-list-change", e)
            }, setAutoplay: function () {
                var t = this;
                window.clearInterval(this.timer), this.autoplay && (this.timer = window.setInterval(function () {
                    t.add(1)
                }, this.autoplaySpeed))
            }
        }
    }
}, function (t, e, i) {
    "use strict";

    function n(t) {
        return t && t.__esModule ? t : {default: t}
    }

    Object.defineProperty(e, "__esModule", {value: !0});
    var s = i(10), a = n(s);
    e.default = {
        name: "SlideList", props: {slideList: {type: Array, default: []}}, data: function () {
            return {slideTabs: ["要闻", "社会", "娱乐", "体育", "军事", "明星"], currentIndex: 0}
        }, mounted: function () {
            var t = this;
            a.default.$on("slide-list-change", function (e) {
                t.currentIndex !== e && (t.currentIndex = e)
            })
        }, methods: {
            tabOver: function (t) {
                this.currentIndex !== t && (this.currentIndex = t, a.default.$emit("slide-tab-change", t), window.ttAnalysis && window.ttAnalysis.send("event", {ev: "focus_tab_hover"}))
            }
        }
    }
}, function (t, e, i) {
    "use strict";

    function n(t) {
        return t && t.__esModule ? t : {default: t}
    }

    Object.defineProperty(e, "__esModule", {value: !0});
    var s = i(3), a = n(s), o = i(172), r = n(o), l = i(173), u = n(l);
    e.default = {
        name: "", props: {}, data: function () {
            return {slideList: [], error: !1}
        }, mounted: function () {
            var t = this;
            (0, a.default)({
                url: "/api/pc/focus/", method: "get", success: function (e) {
                    if ("success" === e.message) {
                        var i = e.data.pc_feed_focus;
                        t.slideList = i.slice(0, 6), 0 === t.slideList.length && t.errorHandle()
                    } else t.errorHandle()
                }, error: function () {
                    t.errorHandle()
                }
            })
        }, methods: {
            errorHandle: function () {
                this.error = !0
            }
        }, components: {slideList: r.default, slideTab: u.default}
    }
}, , , , , , , , , , , , , function (t, e) {
}, function (t, e) {
}, function (t, e) {
}, function (t, e) {
}, function (t, e) {
}, function (t, e) {
}, function (t, e) {
}, function (t, e) {
}, function (t, e) {
}, function (t, e) {
}, function (t, e) {
}, function (t, e) {
}, function (t, e) {
}, function (t, e) {
}, function (t, e) {
}, function (t, e) {
}, function (t, e) {
}, function (t, e) {
}, function (t, e) {
}, function (t, e) {
}, function (t, e) {
}, function (t, e) {
}, function (t, e) {
}, function (t, e) {
}, function (t, e) {
}, , function (t, e, i) {
    t.exports = i.p + "static/img/logo.201f80d.png"
}, function (t, e, i) {
    t.exports = i.p + "static/img/gongan.d0289dc.png"
}, function (t, e, i) {
    t.exports = i.p + "static/img/logo.201f80d.png"
}, , , , , , , function (t, e, i) {
    i(130);
    var n = i(1)(i(80), i(188), null, null);
    t.exports = n.exports
}, function (t, e, i) {
    i(125);
    var n = i(1)(i(83), i(183), null, null);
    t.exports = n.exports
}, function (t, e, i) {
    i(139);
    var n = i(1)(i(84), i(197), null, null);
    t.exports = n.exports
}, function (t, e, i) {
    i(118);
    var n = i(1)(i(85), i(175), null, null);
    t.exports = n.exports
}, function (t, e, i) {
    i(140);
    var n = i(1)(i(86), i(198), null, null);
    t.exports = n.exports
}, function (t, e, i) {
    i(127);
    var n = i(1)(i(87), i(185), null, null);
    t.exports = n.exports
}, function (t, e, i) {
    i(136);
    var n = i(1)(i(88), i(194), null, null);
    t.exports = n.exports
}, function (t, e, i) {
    i(138);
    var n = i(1)(i(89), i(196), null, null);
    t.exports = n.exports
}, function (t, e, i) {
    i(124);
    var n = i(1)(i(90), i(182), null, null);
    t.exports = n.exports
}, function (t, e, i) {
    i(126);
    var n = i(1)(i(91), i(184), null, null);
    t.exports = n.exports
}, function (t, e, i) {
    i(119);
    var n = i(1)(i(92), i(176), null, null);
    t.exports = n.exports
}, function (t, e, i) {
    i(129);
    var n = i(1)(i(93), i(187), null, null);
    t.exports = n.exports
}, function (t, e, i) {
    i(132);
    var n = i(1)(i(94), i(190), null, null);
    t.exports = n.exports
}, function (t, e, i) {
    i(133);
    var n = i(1)(i(95), i(191), "data-v-756beef7", null);
    t.exports = n.exports
}, function (t, e, i) {
    i(134);
    var n = i(1)(i(96), i(192), null, null);
    t.exports = n.exports
}, function (t, e, i) {
    var n = i(1)(i(97), i(178), null, null);
    t.exports = n.exports
}, function (t, e, i) {
    i(135);
    var n = i(1)(i(98), i(193), null, null);
    t.exports = n.exports
}, function (t, e, i) {
    i(141);
    var n = i(1)(i(99), i(199), null, null);
    t.exports = n.exports
}, function (t, e, i) {
    i(120);
    var n = i(1)(i(100), i(177), null, null);
    t.exports = n.exports
}, function (t, e, i) {
    i(123);
    var n = i(1)(i(101), i(181), null, null);
    t.exports = n.exports
}, function (t, e, i) {
    i(137);
    var n = i(1)(i(102), i(195), null, null);
    t.exports = n.exports
}, function (t, e, i) {
    i(131);
    var n = i(1)(i(103), i(189), null, null);
    t.exports = n.exports
}, function (t, e, i) {
    i(121);
    var n = i(1)(i(104), i(179), null, null);
    t.exports = n.exports
}, function (t, e) {
    t.exports = {
        render: function () {
            var t = this, e = t.$createElement, i = t._self._c || e;
            return i("div", {
                staticClass: "no-mode",
                attrs: {ga_event: t.item.article_genre + "_item_click"}
            }, [i("div", {
                staticClass: "title-box",
                attrs: {ga_event: t.item.article_genre + "_title_click"}
            }, [i("a", {
                staticClass: "link",
                attrs: {href: t.item.source_url, target: "_blank"}
            }, [t._v(t._s(t.item.title))])]), t._v(" "), i("FooterBar", {attrs: {item: t.item}})], 1)
        }, staticRenderFns: []
    }
}, function (t, e) {
    t.exports = {
        render: function () {
            var t = this, e = t.$createElement, i = t._self._c || e;
            return t.listCount.length ? i("pane", {attrs: {title: t.title}}, [i("template", {
                attrs: {slot: "content"},
                slot: "content"
            }, [i("ul", {
                staticClass: "module-content video-list",
                attrs: {ga_event: "click_video_recommend"}
            }, t._l(t.listCount, function (e, n) {
                return i("li", {
                    staticClass: "video-item",
                    class: {"video-item-a": 3 === n}
                }, [3 === n ? i("div", {
                    staticStyle: {display: "block !important"},
                    attrs: {name: "hot_video*cell_4", "ad-cursor": ""}
                }) : [i("a", {
                    staticClass: "link",
                    attrs: {href: e.display_url, target: "_blank"}
                }, [i("dl", [i("dt", {staticClass: "module-pic"}, [i("img", {
                    directives: [{
                        name: "lazy",
                        rawName: "v-lazy",
                        value: e.pc_image_url,
                        expression: "item.pc_image_url"
                    }], attrs: {alt: e.title}
                }), t._v(" "), i("i", {staticClass: "module-tag video-tag"}, [i("span", [t._v(t._s(e.video_duration_format))])])]), t._v(" "), i("dd", [i("div", {staticClass: "cell"}, [i("h4", [t._v(t._s(e.title))]), t._v(" "), i("p", [i("span", [t._v(t._s(t._f("formatCount")(e.video_play_count)) + "次播放 ")]), i("span", [t._v("⋅ " + t._s(t._f("formatCount")(e.comment_count)) + "评论")])])])])])])]], 2)
            }))])], 2) : t._e()
        }, staticRenderFns: []
    }
}, function (t, e) {
    t.exports = {
        render: function () {
            var t = this, e = t.$createElement, i = t._self._c || e;
            return i("pane-module", {attrs: {title: t.title}}, [i("template", {
                attrs: {slot: "content"},
                slot: "content"
            }, [i("ul", {
                staticClass: "module-content article-list",
                attrs: {ga_event: "click_hot_news"}
            }, t._l(t.resList, function (e) {
                return i("li", {staticClass: "article-item"}, [i("a", {
                    staticClass: "news-link",
                    attrs: {href: e.open_url, target: "_blank"}
                }, [e.image_url ? i("div", {staticClass: "module-pic news-pic"}, [i("img", {
                    directives: [{
                        name: "lazy",
                        rawName: "v-lazy",
                        value: e.image_url,
                        expression: "item.image_url"
                    }]
                })]) : t._e(), t._v(" "), i("div", {staticClass: "news-inner"}, [i("p", {staticClass: "module-title"}, [t._v(t._s(e.title))])])])])
            }))])], 2)
        }, staticRenderFns: []
    }
}, function (t, e) {
    t.exports = {
        render: function () {
            var t = this, e = t.$createElement, i = t._self._c || e;
            return i("channel", {attrs: {header: t.header, channels: t.channels}})
        }, staticRenderFns: []
    }
}, function (t, e) {
    t.exports = {
        render: function () {
            var t = this, e = t.$createElement, i = t._self._c || e;
            return i("div", {
                staticClass: "bui-box slide",
                class: [t.error ? "slide-hidden" : ""]
            }, [i("slide-list", {
                staticClass: "bui-left",
                attrs: {slideList: t.slideList, autoplay: !0, autoplaySpeed: 5e3}
            }), t._v(" "), i("slide-tab", {staticClass: "bui-right", attrs: {slideList: t.slideList}})], 1)
        }, staticRenderFns: []
    }
}, function (t, e) {
    t.exports = {
        render: function () {
            var t = this, e = t.$createElement, i = t._self._c || e;
            return i("div", {
                staticClass: "action-dislike",
                attrs: {ga_event: "dislike_click"},
                on: {click: t.dislikeClick}
            }, [i("tt-icon", {attrs: {type: "close_small", color: "#ddd", size: "16"}}), t._v("\n  不感兴趣\n")], 1)
        }, staticRenderFns: []
    }
}, function (t, e) {
    t.exports = {
        render: function () {
            var t = this, e = t.$createElement, i = t._self._c || e;
            return i("pane-module", {attrs: {title: t.title}}, [i("template", {
                attrs: {slot: "content"},
                slot: "content"
            }, [i("ul", {staticClass: "more-items-content"}, t._l(t.moreLinks, function (e) {
                return i("li", {staticClass: "item"}, [i("a", {
                    attrs: {
                        href: e.url,
                        target: e.target
                    }
                }, [t._v(t._s(e.title))])])
            }))])], 2)
        }, staticRenderFns: []
    }
}, function (t, e) {
    t.exports = {
        render: function () {
            var t = this, e = t.$createElement, i = t._self._c || e;
            return i("div", {staticClass: "toutiao-header"}, [i("div", {staticClass: "topbar"}, [t.options.isHomePage ? i("div", {staticClass: "bui-left clearfix"}, [i("a", {
                staticClass: "download-app tb-link",
                attrs: {href: "//app.toutiao.com/news_article/", target: "_blank", ga_event: "mh_nav_others"}
            }, [t._v("下载APP")]), t._v(" "), i("weather-box")], 1) : t._e(), t._v(" "), t.options.isHomePage ? t._e() : i("div", {staticClass: "bui-left clearfix"}, [i("ul", {
                staticClass: "nav-list",
                attrs: {ga_event: "mh_channel"}
            }, [t._l(t.navItem, function (e) {
                return i("li", {staticClass: "bui-left"}, [i("a", {
                    staticClass: "tb-link",
                    attrs: {href: e.url, target: "_blank", ga_event: "mh_channel_" + e.en}
                }, [t._v(t._s(e.name))])])
            }), t._v(" "), i("li", {staticClass: "bui-left"}, [i("tt-dropdown", [i("a", {
                staticClass: "tt-dropdown-link tb-link",
                attrs: {href: "javascript:void(0);"}
            }, [t._v("\n              更多\n              "), i("tt-icon", {
                attrs: {
                    type: "arrow_down",
                    color: "#fff",
                    size: "14"
                }
            })], 1), t._v(" "), i("tt-dropdown-menu", {
                attrs: {slot: "dropdown"},
                slot: "dropdown"
            }, t._l(t.navMore, function (e) {
                return i("tt-dropdown-item", {key: e.en}, [i("a", {
                    staticClass: "tb-link",
                    attrs: {href: e.url, target: "_blank", ga_event: "mh_channel_" + e.en}
                }, [t._v(t._s(e.name))])])
            }))], 1)], 1)], 2)]), t._v(" "), i("div", {staticClass: "bui-right"}, [i("ul", {staticClass: "user-nav-list clearfix"}, [t.options.id && t.options.isPgc ? i("li", {staticClass: "new-article"}, [i("a", {
                staticClass: "tb-link",
                attrs: {href: "//mp.toutiao.com/new_article/", ga_event: "mh_write", target: "_blank"}
            }, [t._v("发文")])]) : t._e(), t._v(" "), t.options.id && !t.options.isPgc ? i("li", {staticClass: "new-article"}, [i("a", {
                staticClass: "tb-link",
                attrs: {
                    href: "//www.toutiao.com/c/user/" + t.options.id + "/?publish=1",
                    ga_event: "ugc_write",
                    target: "_blank"
                }
            }, [t._v("发文")])]) : t._e(), t._v(" "), t.options.id && t.showUser ? i("li", [i("tt-dropdown", [i("div", {staticClass: "tt-dropdown-link tb-link"}, [i("a", {
                staticClass: "user-name",
                attrs: {
                    href: "//www.toutiao.com/c/user/" + t.options.id + "/",
                    ga_event: "mh_nav_user",
                    target: "_blank",
                    rel: "nofollow"
                }
            }, [i("img", {
                staticClass: "user-avatar",
                attrs: {src: t.options.avatarUrl}
            }), t._v("\n              " + t._s(t.options.userName))])]), t._v(" "), i("tt-dropdown-menu", {
                attrs: {slot: "dropdown"},
                slot: "dropdown"
            }, [i("tt-dropdown-item", [i("a", {
                staticClass: "layer-item",
                attrs: {
                    href: "//www.toutiao.com/c/user/" + t.options.id + "/?tab=favourite",
                    target: "_blank",
                    rel: "nofollow"
                }
            }, [t._v("我的收藏")])]), t._v(" "), i("tt-dropdown-item", [i("a", {
                staticClass: "layer-item",
                attrs: {
                    href: "//www.toutiao.com/c/user/" + t.options.id + "/?tab=following",
                    target: "_blank",
                    rel: "nofollow"
                }
            }, [t._v("我的关注")])]), t._v(" "), i("tt-dropdown-item", [i("a", {
                staticClass: "layer-item",
                attrs: {
                    href: "//www.toutiao.com/c/user/relation/" + t.options.id + "/?tab=followed",
                    target: "_blank",
                    rel: "nofollow"
                }
            }, [t._v("我的粉丝")])]), t._v(" "), i("tt-dropdown-item", {attrs: {divided: ""}}, [i("a", {
                staticClass: "layer-item",
                attrs: {href: "https://sso.toutiao.com/logout/", rel: "nofollow"}
            }, [t._v("退出")])])], 1)], 1)], 1) : t._e(), t._v(" "), !t.options.id && t.showUser ? i("li", {staticClass: "nav-login"}, [i("a", {
                staticClass: "tb-link",
                attrs: {ga_event: "nav_login"},
                on: {
                    click: function (e) {
                        e.preventDefault(), t.jumpToLogin(e)
                    }
                }
            }, [t._v("登录")])]) : t._e(), t._v(" "), t.options.id ? i("li", [i("feedback", {attrs: {show: !1}})], 1) : t._e(), t._v(" "), t._m(0), t._v(" "), i("li", [i("tt-dropdown", [i("a", {
                staticClass: "tt-dropdown-link tb-link",
                attrs: {href: "javascript:;"}
            }, [t._v("头条产品")]), t._v(" "), i("tt-dropdown-menu", {
                attrs: {slot: "dropdown"},
                slot: "dropdown"
            }, [i("tt-dropdown-item", [i("a", {
                staticClass: "layer-item",
                attrs: {href: "https://www.wukong.com/", target: "_blank", ga_event: "mh_nav_others"}
            }, [t._v("问答")])]), t._v(" "), i("tt-dropdown-item", [i("a", {
                staticClass: "layer-item",
                attrs: {href: "https://mp.toutiao.com/", target: "_blank", ga_event: "mh_nav_others"}
            }, [t._v("头条号")])]), t._v(" "), i("tt-dropdown-item", [i("a", {
                staticClass: "layer-item",
                attrs: {
                    href: "https://tuchong.com?utm_source=toutiao&utm_medium=pc_header",
                    target: "_blank",
                    ga_event: "mh_nav_others"
                }
            }, [t._v("图虫")])]), t._v(" "), i("tt-dropdown-item", [i("a", {
                staticClass: "layer-item",
                attrs: {href: "https://stock.tuchong.com/?source=ttweb", target: "_blank", ga_event: "mh_nav_others"}
            }, [t._v("正版图库")])]), t._v(" "), i("tt-dropdown-item", [i("a", {
                staticClass: "layer-item",
                attrs: {
                    href: "https://ad.toutiao.com/promotion/?source2=pchometop",
                    target: "_blank",
                    ga_event: "mh_nav_ad"
                }
            }, [t._v("广告投放")])]), t._v(" "), i("tt-dropdown-item", [i("a", {
                staticClass: "layer-item",
                attrs: {href: "http://s.pstatp.com/site/motor/", target: "_blank", ga_event: "mh_nav_others"}
            }, [t._v("懂车帝")])])], 1)], 1)], 1)])])]), t._v(" "), t.options.hasBar ? i("div", {staticClass: "middlebar"}, [i("div", {
                staticClass: "middlebar-inner clearfix",
                style: {width: t.middlebarWidth + "px"}
            }, [i("div", {staticClass: "bui-left logo-box"}, [i("a", {
                staticClass: "logo-link",
                attrs: {href: "/", ga_event: "go_home"}
            }, [i("img", {
                staticClass: "logo",
                attrs: {src: t.logoImg}
            })])]), t._v(" "), t.options.chineseTag ? i("div", {staticClass: "bui-left chinese-tag"}, [i("a", {
                attrs: {
                    href: "/",
                    target: "_blank",
                    ga_event: "click_index"
                }
            }, [t._v("首页")]), t._v("\n        /\n        "), i("a", {
                attrs: {
                    href: "/" + t.options.crumbTag,
                    target: "_blank",
                    ga_event: "click_channel"
                }
            }, [t._v(t._s(t.options.chineseTag))]), t._v("\n        /\n        "), i("span", {staticClass: "text"}, [t._v("正文")])]) : t._e(), t._v(" "), i("div", {
                staticClass: "bui-right",
                attrs: {ga_event: "middlebar_search"}
            }, [i("search-box")], 1)])]) : t._e()])
        }, staticRenderFns: [function () {
            var t = this, e = t.$createElement, i = t._self._c || e;
            return i("li", [i("a", {
                staticClass: "tb-link",
                attrs: {href: "//www.toutiao.com/complain/", ga_event: "mh_nav_complain", target: "blank"}
            }, [t._v("侵权投诉")])])
        }]
    }
}, function (t, e) {
    t.exports = {
        render: function () {
            var t = this, e = t.$createElement, i = t._self._c || e;
            return i("div", [i("div", {
                staticClass: "msg-alert",
                class: {"msg-alert-hidden": t.msgHidden}
            }, [t.articleCount > 0 ? i("span", [t._v("为您推荐了" + t._s(t.articleCount) + "篇文章")]) : t._e(), t._v(" "), 0 == t.articleCount ? i("span", [t._v("暂时没有更新，休息一会儿吧")]) : t._e()]), t._v(" "), i("div", {
                directives: [{
                    name: "show",
                    rawName: "v-show",
                    value: t.msgShow,
                    expression: "msgShow"
                }], ref: "msgAlertPlace", staticClass: "msgAlert-place", on: {click: t.feedRefreshClick}
            }, [i("div", {
                staticClass: "msg-alert",
                class: {"msg-alert-fixed": t.msgFixed},
                attrs: {ga_event: "refresh_float_click"}
            }, [i("span", [t._v("您有未读新闻，点击查看")]), t._v(" "), i("tt-icon", {
                attrs: {
                    type: "close_small",
                    size: "15",
                    color: "#fff"
                }, nativeOn: {
                    click: function (e) {
                        e.stopPropagation(), t.closeMsgClick(e)
                    }
                }
            })], 1)])])
        }, staticRenderFns: []
    }
}, function (t, e) {
    t.exports = {
        render: function () {
            var t = this, e = t.$createElement, i = t._self._c || e;
            return t.listCount.length ? i("pane", {attrs: {title: t.title}}, [i("template", {
                attrs: {slot: "content"},
                slot: "content"
            }, [i("ul", {
                staticClass: "module-content bui-box picture-list",
                attrs: {ga_event: "click_pictures_recommend"}
            }, t._l(t.listCount, function (e, n) {
                return i("li", {
                    staticClass: "bui-left picture-item",
                    class: {"picture-item-a": 2 === n || 6 === n}
                }, [2 === n ? i("div", {
                    staticStyle: {display: "block !important"},
                    attrs: {name: "hot_image*cell_5", "ad-cursor": ""}
                }) : 6 === n ? i("div", {
                    staticStyle: {display: "block !important"},
                    attrs: {name: "hot_image*cell_6", "ad-cursor": ""}
                }) : [i("a", {
                    staticClass: "link",
                    attrs: {href: e.article_url, target: "_blank"}
                }, [i("div", {staticClass: "module-pic picture-img"}, [i("img", {
                    directives: [{
                        name: "lazy",
                        rawName: "v-lazy",
                        value: e.cover_image_url,
                        expression: "item.cover_image_url"
                    }], attrs: {alt: ""}
                }), t._v(" "), i("i", {staticClass: "module-tag"}, [i("span", [t._v(t._s(e.gallery_image_count) + "图")])])]), t._v(" "), i("p", [t._v(t._s(e.title))])])]], 2)
            }))])], 2) : t._e()
        }, staticRenderFns: []
    }
}, function (t, e) {
    t.exports = {
        render: function () {
            var t = this, e = t.$createElement, i = t._self._c || e;
            return i("div", {
                staticClass: "bui-box single-mode",
                attrs: {ga_event: t.item.article_genre + "_item_click"}
            }, [i("div", {
                staticClass: "bui-left single-mode-lbox",
                attrs: {ga_event: t.item.article_genre + "_img_click"}
            }, [i("a", {
                staticClass: "img-wrap",
                attrs: {href: t.item.source_url, target: "_blank"}
            }, [i("img", {
                directives: [{
                    name: "lazy",
                    rawName: "v-lazy",
                    value: t.item.image_url,
                    expression: "item.image_url"
                }], staticClass: "lazy-load-img"
            }), t._v(" "), t.item.has_video ? i("i", {staticClass: "pic-tip video-tip"}, [i("span", [t._v(t._s(t.item.video_duration_str))])]) : t._e()])]), t._v(" "), i("div", {staticClass: "single-mode-rbox"}, [i("div", {staticClass: "single-mode-rbox-inner"}, [i("div", {
                staticClass: "title-box",
                attrs: {ga_event: t.item.article_genre + "_title_click"}
            }, [i("a", {
                staticClass: "link",
                attrs: {href: t.item.source_url, target: "_blank"}
            }, [t._v(t._s(t.item.title))])]), t._v(" "), i("FooterBar", {
                attrs: {
                    item: t.item,
                    dislikeUrl: t.dislikeUrl,
                    getUserInfoUrl: t.getUserInfoUrl
                }
            })], 1)])])
        }, staticRenderFns: []
    }
}, function (t, e) {
    t.exports = {
        render: function () {
            var t = this, e = t.$createElement, i = t._self._c || e;
            return i("div", {staticClass: "bui-box footer-bar"}, [i("div", {staticClass: "bui-left footer-bar-left"}, [t.item.tag_style ? i("a", {
                staticClass: "footer-bar-action tag",
                class: t.item.tag_style,
                attrs: {href: t.item.tag_url, target: "_blank", ga_event: "article_tag_click"}
            }, [t._v(t._s(t.item.chinese_tag))]) : t._e(), t._v(" "), t.item.media_url ? [i("a", {
                staticClass: "footer-bar-action media-avatar",
                attrs: {href: t.item.media_url, target: "_blank", ga_event: t.item.article_genre + "_avatar_click"}
            }, [i("img", {
                directives: [{
                    name: "lazy",
                    rawName: "v-lazy",
                    value: t.item.media_avatar_url,
                    expression: "item.media_avatar_url"
                }]
            })]), t._v(" "), i("a", {
                staticClass: "footer-bar-action source",
                attrs: {href: t.item.media_url, target: "_blank", ga_event: t.item.article_genre + "_name_click"}
            }, [t._v(" " + t._s(t.item.source) + " ⋅")]), t._v(" "), i("a", {
                staticClass: "footer-bar-action source",
                attrs: {
                    href: t.item.source_url + "/#comment_area",
                    target: "_blank",
                    ga_event: t.item.article_genre + "_comment_click"
                }
            }, [t._v(" " + t._s(t.item.comments_count) + "评论 ⋅")])] : [i("a", {
                staticClass: "footer-bar-action media-avatar",
                class: t.item.avatar_style,
                attrs: {href: "/search/?keyword=" + t.item.source, ga_event: t.item.article_genre + "_avatar_click"}
            }, [t._v(t._s(t.item.source_tag))]), t._v(" "), i("a", {
                staticClass: "footer-bar-action source",
                attrs: {
                    href: "/search/?keyword=" + t.item.source,
                    target: "_blank",
                    ga_event: t.item.article_genre + "_name_click"
                }
            }, [t._v(" " + t._s(t.item.source) + " ⋅")])], t._v(" "), i("span", {staticClass: "footer-bar-action"}, [t._v(" " + t._s(t.item.time_ago))]), t._v(" "), t.item.is_related ? i("span", {staticClass: "footer-bar-action recommend"}, [t._v("相关")]) : t._e(), t._v(" "), t.item.hot ? i("span", {staticClass: "footer-bar-action hot"}, [t._v("热")]) : t._e(), t._v(" "), t.item.ad_label ? i("a", {
                staticClass: "footer-bar-action ad",
                attrs: {target: "_blank", href: "https://ad.toutiao.com/promotion/?source2=pcfeedadtag"}
            }, [t._v(t._s(t.item.ad_label))]) : t._e()], 2), t._v(" "), t.item.group_id ? i("div", {staticClass: "bui-right"}, [i("Dislike", {
                attrs: {
                    group_id: t.item.group_id + "",
                    item_id: t.item.item_id + "",
                    ad_id: t.item.ad_id + "",
                    dislikeUrl: t.dislikeUrl,
                    getUserInfoUrl: t.getUserInfoUrl
                }
            })], 1) : t._e()])
        }, staticRenderFns: []
    }
}, function (t, e) {
    t.exports = {
        render: function () {
            var t = this, e = t.$createElement, i = t._self._c || e;
            return i("div", {staticClass: "pane-module"}, [t.title ? i("div", {staticClass: "module-head"}, [t._v(t._s(t.title))]) : t._e(), t._v(" "), t._t("content")], 2)
        }, staticRenderFns: []
    }
}, function (t, e) {
    t.exports = {
        render: function () {
            var t = this, e = t.$createElement, i = t._self._c || e;
            return i("div", [i("div", {
                staticClass: "channel",
                class: {"channel-fixed": t.isFixed},
                attrs: {ga_event: "left-channel-click"}
            }, [t.header && t.header.logoImg ? i("a", {
                staticClass: "logo",
                attrs: {href: "/"}
            }, [i("img", {
                style: {width: t.header.logoWidth, height: t.header.logoHeight},
                attrs: {src: t.header.logoImg, alt: t.header.logoAlt}
            })]) : t._e(), t._v(" "), t.header && t.header.title ? i("h3", {staticClass: "related-header"}, [t._v(t._s(t.header.title))]) : t._e(), t._v(" "), i("ul", [t._l(t.channels.items, function (e) {
                return i("li", [i("a", {
                    staticClass: "channel-item",
                    class: {active: t.channels.tag === e.url || t.channels.tag === e.tag},
                    attrs: {
                        href: e.url,
                        target: e.target ? e.target : "_self",
                        ga_event: e.log ? "channel_" + e.log + "_click" : ""
                    },
                    on: {
                        click: function (i) {
                            t.itemClick(e)
                        }
                    }
                }, [i("span", [t._v(t._s(e.name))])])])
            }), t._v(" "), t.channels.more ? i("li", {staticClass: "channel-more"}, [t._m(0), t._v(" "), i("div", {staticClass: "channel-more-layer"}, [i("ul", {staticClass: "bui-box"}, t._l(t.channels.more, function (e) {
                return i("li", {staticClass: "bui-left"}, [i("a", {
                    staticClass: "channel-item",
                    attrs: {
                        href: e.url,
                        target: e.target ? "_blank" : "_self",
                        ga_event: e.log ? "channel_" + e.log + "_click" : ""
                    },
                    on: {
                        click: function (i) {
                            t.itemClick(e)
                        }
                    }
                }, [i("span", [t._v(t._s(e.name))])])])
            }))])]) : t._e()], 2)])])
        }, staticRenderFns: [function () {
            var t = this, e = t.$createElement, i = t._self._c || e;
            return i("a", {
                staticClass: "channel-item",
                attrs: {href: "javascript:void(0);"}
            }, [i("span", [t._v("更多")])])
        }]
    }
}, function (t, e) {
    t.exports = {
        render: function () {
            var t = this, e = t.$createElement, i = t._self._c || e;
            return i("ul", {staticClass: "slide-tab"}, t._l(t.slideTabs, function (e, n) {
                return i("li", {
                    staticClass: "slide-tab-item",
                    class: [n === t.currentIndex ? "slide-tab-item-active" : ""],
                    on: {
                        mouseover: function (e) {
                            t.tabOver(n)
                        }
                    }
                }, [t._v("\n    " + t._s(e) + "\n  ")])
            }))
        }, staticRenderFns: []
    }
}, function (t, e) {
    t.exports = {
        render: function () {
            var t = this, e = t.$createElement, i = t._self._c || e;
            return i("div", {staticClass: "search-wrap"}, [i("tt-autocomplete", {
                attrs: {
                    "fetch-suggestions": t.querySearchAsync,
                    "on-enter-keydown": t.handleEnterKeydown,
                    "custom-item": t.resultType,
                    placeholder: t.placeHolder
                },
                on: {focus: t.handleFocus, select: t.handleSelect, blur: t.handleBlur},
                model: {
                    value: t.searchWord, callback: function (e) {
                        t.searchWord = e
                    }, expression: "searchWord"
                }
            }, [i("template", {
                attrs: {slot: "append"},
                slot: "append"
            }, [i("tt-button", {on: {click: t.handleIconClick}}, [t._v("搜索")])], 1)], 2)], 1)
        }, staticRenderFns: []
    }
}, function (t, e) {
    t.exports = {
        render: function () {
            var t = this, e = t.$createElement, i = t._self._c || e;
            return i("ul", {staticClass: "toolbar"}, [t.showHome ? i("li", {staticClass: "tool-item"}, [i("a", {attrs: {href: "//www.toutiao.com"}}, [i("tt-icon", {
                attrs: {
                    type: "house",
                    size: "16",
                    color: "#fff"
                }
            })], 1)]) : t._e(), t._v(" "), t.showReport ? i("li", {staticClass: "tool-item report-item"}, [t._m(0)]) : t._e(), t._v(" "), t.showRefresh ? i("li", {
                staticClass: "tool-item",
                on: {click: t.refresh}
            }, [i("a", {attrs: {href: "javascript:void(0);"}}, [i("tt-icon", {
                attrs: {
                    type: "refresh",
                    size: "16",
                    color: "#fff"
                }
            })], 1)]) : t._e(), t._v(" "), t.reallyShowTop ? i("li", {
                staticClass: "tool-item",
                on: {click: t.scrollToTop}
            }, [i("a", {attrs: {href: "javascript:void(0);"}}, [i("tt-icon", {
                attrs: {
                    type: "arrow_up_big",
                    size: "16",
                    color: "#fff"
                }
            })], 1)]) : t._e()])
        }, staticRenderFns: [function () {
            var t = this, e = t.$createElement, i = t._self._c || e;
            return i("a", {
                attrs: {
                    href: "http://report.12377.cn:13225/toreportinputNormal_anis.do",
                    target: "_blank"
                }
            }, [i("img", {attrs: {src: "//s3a.pstatp.com/toutiao/resource/ntoutiao_web/static/image/other/report_logo_15cc24e.png"}}), t._v(" "), i("span", [t._v("网上有害信息举报")])])
        }]
    }
}, function (t, e) {
    t.exports = {
        render: function () {
            var t = this, e = t.$createElement, i = t._self._c || e;
            return i("div", {
                staticClass: "weather-tool",
                on: {mouseenter: t.handleMouseEnter, mouseleave: t.handleMouseLeave}
            }, [i("div", {staticClass: "weather-abstract"}, [i("span", [t._v(" " + t._s(t.weathercity))]), t._v(" "), i("span", {staticClass: "city_state"}, [t._v(t._s(t.weather.current_condition))]), t._v(" "), i("span", {staticClass: "city_temperature"}, [i("em", [t._v(t._s(t.weather.low_temperature))]), t._v("°  /  "), i("em", [t._v(t._s(t.weather.high_temperature))]), t._v("°\n    ")]), t._v(" "), i("tt-icon", {
                attrs: {
                    type: "arrow_down",
                    color: "#fff",
                    size: "14"
                }
            })], 1), t._v(" "), i("div", {
                directives: [{
                    name: "show",
                    rawName: "v-show",
                    value: t.showPopup,
                    expression: "showPopup"
                }], staticClass: "y-weather"
            }, [i("div", {staticClass: "w-header"}, [i("span", {
                staticClass: "bui-icon icon-locationweather bui-vm",
                attrs: {ga_event: "mh_change_city"},
                on: {click: t.changeLocation}
            }, [t._v(" " + t._s(t.weathercity))]), t._v(" "), i("span", {staticClass: "wind bui-vm"}, [t._v(t._s(t.weather.wind_direction) + t._s(t.weather.wind_level) + "级")]), t._v(" "), i("span", {
                staticClass: "air vm",
                style: {backgroundColor: t.aqiColor}
            }, [t._v(t._s(t.weather.quality_level) + " " + t._s(t.weather.aqi))])]), t._v(" "), t.showWeather ? i("ul", {staticClass: "days-weather"}, [i("li", {staticClass: "bui-left day"}, [i("span", {staticClass: "title"}, [t._v("今天")]), t._v(" "), i("div", {
                staticClass: "weather-icon",
                class: [t.iconClass.today],
                attrs: {title: t.weather.current_condition}
            }), t._v(" "), i("span", {staticClass: "temperature"}, [i("em", [t._v(t._s(t.weather.low_temperature))]), t._v("°\n            /\n            "), i("em", [t._v(t._s(t.weather.high_temperature))]), t._v("°\n        ")])]), t._v(" "), i("li", {staticClass: "bui-left day"}, [i("span", {staticClass: "title"}, [t._v("明天")]), t._v(" "), i("div", {
                staticClass: "weather-icon",
                class: [t.iconClass.tom],
                attrs: {title: t.weather.tomorrow_condition}
            }), t._v(" "), i("span", {staticClass: "temperature"}, [i("em", [t._v(t._s(t.weather.tomorrow_low_temperature))]), t._v("°\n            /\n            "), i("em", [t._v(t._s(t.weather.tomorrow_high_temperature))]), t._v("°\n        ")])]), t._v(" "), i("li", {staticClass: "bui-left day"}, [i("span", {staticClass: "title"}, [t._v("后天")]), t._v(" "), i("div", {
                staticClass: "weather-icon",
                class: [t.iconClass.dat],
                attrs: {title: t.weather.dat_condition}
            }), t._v(" "), i("span", {staticClass: "temperature"}, [i("em", [t._v(t._s(t.weather.dat_low_temperature))]), t._v("°\n            /\n            "), i("em", [t._v(t._s(t.weather.dat_high_temperature))]), t._v("°\n        ")])])]) : i("div", {staticClass: "city-select"}, [i("div", {staticClass: "clearfix"}, [i("div", {staticClass: "bui-left select-style"}, [i("tt-select", {
                attrs: {placeholder: "省份"},
                on: {change: t.handleProvinceChange, "visible-change": t.isSelecting},
                model: {
                    value: t.province, callback: function (e) {
                        t.province = e
                    }, expression: "province"
                }
            }, t._l(t.provinces, function (t) {
                return i("tt-option", {key: t.value, attrs: {label: t.label, value: t.value}})
            }))], 1), t._v(" "), i("div", {staticClass: "bui-right select-style"}, [i("tt-select", {
                attrs: {placeholder: "城市"},
                on: {"visible-change": t.isSelecting},
                model: {
                    value: t.city, callback: function (e) {
                        t.city = e
                    }, expression: "city"
                }
            }, t._l(t.citys, function (t) {
                return i("tt-option", {key: t.value, attrs: {label: t.label, value: t.value}})
            }))], 1)]), t._v(" "), i("div", {staticClass: "action clearfix"}, [i("a", {
                staticClass: "bui-left ok-btn",
                attrs: {href: "javascript:;"},
                on: {click: t.onSubmitClick}
            }, [t._v("确定")]), t._v(" "), i("a", {
                staticClass: "bui-right cancel-btn",
                attrs: {href: "javascript:;"},
                on: {click: t.onCancelClick}
            }, [t._v("取消")])])])])])
        }, staticRenderFns: []
    }
}, function (t, e) {
    t.exports = {
        render: function () {
            var t = this, e = t.$createElement, i = t._self._c || e;
            return i("div", {staticClass: "company"}, [i("p", {staticClass: "J-company-name"}, [t._v(" © " + t._s(t.year) + " 今日头条")]), t._v(" "), i("a", {
                attrs: {
                    href: "http://www.12377.cn/",
                    target: "_blank",
                    ga_event: "click_about"
                }
            }, [t._v("中国互联网举报中心")]), t._v(" "), i("a", {
                attrs: {
                    href: "http://www.miibeian.gov.cn/",
                    target: "_blank",
                    ga_event: "click_about"
                }
            }, [t._v("京ICP证140141号")]), t._v(" "), t._m(0), t._v(" "), i("p", [t._v("京-非经营性-2016-0081")]), t._v(" "), i("p", [t._v("互联网药品信息服务资格证书")]), t._v(" "), i("a", {
                attrs: {
                    href: "/a3642705768/",
                    target: "_blank"
                }
            }, [t._v("跟帖评论自律管理承诺书")]), t._v(" "), i("span", [t._v("违法和不良信息举报：010-58341833")]), t._v(" "), i("span", [t._v("公司名称：北京字节跳动科技有限公司")]), t._v(" "), i("a", {
                attrs: {
                    href: "http://www.beian.gov.cn/portal/registerSystemInfo?recordcode=11000002002023",
                    target: "_blank",
                    ga_event: "click_about"
                }
            }, [i("img", {attrs: {src: t.gonganImg}}), t._v(" 京公网安备 11000002002023号")])])
        }, staticRenderFns: [function () {
            var t = this, e = t.$createElement, i = t._self._c || e;
            return i("div", [i("a", {
                attrs: {
                    href: "http://www.miibeian.gov.cn/",
                    target: "_blank",
                    ga_event: "click_about"
                }
            }, [t._v("京ICP备12025439号-3\n    ")]), t._v(" "), i("a", {
                staticClass: "icp",
                attrs: {href: "/license/", target: "_blank"}
            }, [t._v("网络文化经营许可证")])])
        }]
    }
}, function (t, e) {
    t.exports = {
        render: function () {
            var t = this, e = t.$createElement, i = t._self._c || e;
            return i("div", {
                staticClass: "bui-box ugc-mode",
                style: t.styles,
                attrs: {ga_event: "ugc_item_click"}
            }, [t.item.ugc_data.ugc_images.length ? i("div", {
                staticClass: "bui-left ugc-mode-lbox",
                attrs: {ga_event: "ugc_img_click"}
            }, [i("a", {
                staticClass: "img-wrap",
                attrs: {href: t.item.source_url, target: "_blank"}
            }, [i("img", {
                directives: [{
                    name: "lazy",
                    rawName: "v-lazy",
                    value: t.item.ugc_data.ugc_images[0],
                    expression: "item.ugc_data.ugc_images[0]"
                }], staticClass: "lazy-load-img"
            })]), t._v(" "), t.item.ugc_data.ugc_images.length > 1 ? i("span", {staticClass: "pic-tip"}, [t._v(t._s(t.item.ugc_data.ugc_images.length) + "图")]) : t._e()]) : t._e(), t._v(" "), i("div", {staticClass: "ugc-mode-right ugc-mode-rbox"}, [i("div", {staticClass: "ugc-mode-rbox-inner"}, [i("div", {staticClass: "ugc-mode-user"}, [i("a", {
                staticClass: "ugc-avatar",
                attrs: {href: t.item.ugc_data.ugc_user.open_url, target: "_blank", ga_event: "ugc_avatar_click"}
            }, [i("img", {
                directives: [{
                    name: "lazy",
                    rawName: "v-lazy",
                    value: t.item.ugc_data.ugc_user.avatar_url,
                    expression: "item.ugc_data.ugc_user.avatar_url"
                }]
            })]), t._v(" "), i("div", {staticClass: "ugc-desc"}, [i("a", {
                staticClass: "ugc-name",
                attrs: {href: t.item.ugc_data.ugc_user.open_url, target: "_blank", ga_event: "ugc_name_click"}
            }, [i("span", [t._v(t._s(t.item.ugc_data.ugc_user.name))]), t._v(" "), t.item.ugc_data.ugc_user.user_verified ? [i("tt-icon", {
                attrs: {
                    type: "symbolv",
                    size: "16",
                    color: "#FEC346"
                }
            })] : t._e(), t._v(" "), i("tt-icon", {
                attrs: {
                    type: "vtt",
                    size: "20",
                    color: "#FF9818"
                }
            })], 2), t._v(" "), i("p", {staticClass: "ugc-meta"}, [t.item.ugc_data.ugc_user.is_following ? i("span", [t._v("已关注")]) : i("span", [t._v("未关注")]), t._v(" "), t.item.ugc_data.ugc_user.user_auth_info.auth_info ? i("span", [t._v(" · " + t._s(t.item.ugc_data.ugc_user.user_auth_info.auth_info))]) : t._e()])])]), t._v(" "), i("div", {
                staticClass: "ugc-mode-content",
                attrs: {ga_event: "ugc_content_click"}
            }, [t.item.ugc_data.rich_content ? i("a", {
                attrs: {href: t.item.source_url, target: "_blank"},
                domProps: {innerHTML: t._s(t.item.ugc_data.rich_content)}
            }) : i("a", {
                attrs: {
                    href: t.item.source_url,
                    target: "_blank"
                }
            }, [t._v(t._s(t.item.ugc_data.content))])]), t._v(" "), i("div", {staticClass: "bui-box ugc-mode-footer"}, [i("div", {
                staticClass: "bui-left ugc-mode-footer-left",
                attrs: {ga_event: "ugc_comment_click"}
            }, [i("a", {
                staticClass: "ugc-mode-action source",
                attrs: {href: t.item.source_url, target: "_blank"}
            }, [t._v(t._s(t._f("formatCount")(t.item.ugc_data.digg_count)) + "赞")]), t._v(" "), i("a", {
                staticClass: "ugc-mode-action source",
                attrs: {href: t.item.source_url + "/#comment_area", target: "_blank"}
            }, [t._v(" · " + t._s(t._f("formatCount")(t.item.ugc_data.comment_count)) + "评论")]), t._v(" "), i("a", {
                staticClass: "ugc-mode-action source",
                attrs: {href: t.item.source_url, target: "_blank"}
            }, [t._v(" · " + t._s(t._f("formatCount")(t.item.ugc_data.read_count)) + "阅读")]), t._v(" "), i("span", {staticClass: "ugc-mode-action"}, [t._v(" · " + t._s(t.item.time_ago))])]), t._v(" "), i("div", {staticClass: "bui-right"}, [i("Dislike", {attrs: {group_id: t.item.group_id}})], 1)])])])])
        }, staticRenderFns: []
    }
}, function (t, e) {
    t.exports = {
        render: function () {
            var t = this, e = t.$createElement, i = t._self._c || e;
            return i("ul", {staticClass: "slide-list"}, t._l(t.slideList, function (e, n) {
                return i("li", {
                    staticClass: "slide-item",
                    class: [n === t.currentIndex ? "slide-item-active" : ""],
                    attrs: {ga_event: "focus_list_click"}
                }, [i("a", {attrs: {href: e.display_url, target: "_blank"}}, [i("img", {
                    attrs: {
                        src: e.image_url,
                        alt: ""
                    }
                }), t._v(" "), i("p", {staticClass: "title"}, [t._v(t._s(e.title))])])])
            }))
        }, staticRenderFns: []
    }
}, function (t, e) {
    t.exports = {
        render: function () {
            var t = this, e = t.$createElement, i = t._self._c || e;
            return i("div", {staticClass: "feedback"}, [i("a", {
                staticClass: "feedback-text",
                attrs: {href: "javascript:void(0)"},
                on: {
                    click: function (e) {
                        t.feedbackDialogVisible = !0
                    }
                }
            }, [t._v("反馈")]), t._v(" "), i("tt-dialog", {
                attrs: {title: "意见反馈", size: "tiny"},
                model: {
                    value: t.feedbackDialogVisible, callback: function (e) {
                        t.feedbackDialogVisible = e
                    }, expression: "feedbackDialogVisible"
                }
            }, [i("div", {staticClass: "content"}, [i("p", {staticClass: "label"}, [t._v("联系方式（必填）")]), t._v(" "), i("div", {staticClass: "input-group"}, [i("input", {
                directives: [{
                    name: "model",
                    rawName: "v-model",
                    value: t.feedbackEmail,
                    expression: "feedbackEmail"
                }],
                staticClass: "email",
                attrs: {placeholder: "您的邮箱/QQ号", type: "text"},
                domProps: {value: t.feedbackEmail},
                on: {
                    input: function (e) {
                        e.target.composing || (t.feedbackEmail = e.target.value)
                    }
                }
            })]), t._v(" "), i("p", {staticClass: "label"}, [t._v("您的意见（必填）")]), t._v(" "), i("div", {staticClass: "input-group"}, [i("textarea", {
                directives: [{
                    name: "model",
                    rawName: "v-model",
                    value: t.feedbackContent,
                    expression: "feedbackContent"
                }],
                staticClass: "text",
                attrs: {maxlength: "140", placeholder: "请填写您的意见，不超过140字"},
                domProps: {value: t.feedbackContent},
                on: {
                    input: function (e) {
                        e.target.composing || (t.feedbackContent = e.target.value)
                    }
                }
            })])]), t._v(" "), i("span", {
                staticClass: "dialog-footer",
                attrs: {slot: "footer"},
                slot: "footer"
            }, [i("tt-button", {
                on: {
                    click: function (e) {
                        t.feedbackDialogVisible = !1
                    }
                }
            }, [t._v("取消")]), t._v(" "), i("tt-button", {
                staticClass: "submit",
                attrs: {type: "primary", disabled: t.isSubmitDisabled},
                on: {click: t.submit}
            }, [t._v("确认")])], 1)])], 1)
        }, staticRenderFns: []
    }
}, function (t, e) {
    t.exports = {
        render: function () {
            var t = this, e = t.$createElement, i = t._self._c || e;
            return i("div", {
                staticClass: "more-mode",
                attrs: {ga_event: t.item.article_genre + "_item_click"}
            }, [i("div", {
                staticClass: "title-box",
                attrs: {ga_event: t.item.article_genre + "_title_click"}
            }, [i("a", {
                staticClass: "link",
                attrs: {href: t.item.source_url, target: "_blank"}
            }, [t._v(t._s(t.item.title))])]), t._v(" "), i("div", {
                staticClass: "bui-box img-list",
                attrs: {ga_event: t.item.article_genre + "_img_click"}
            }, [t._l(t.item.image_list, function (e, n) {
                return i("a", {
                    staticClass: "img-wrap img-item",
                    attrs: {href: e.source_url || t.item.source_url, target: "_blank"}
                }, [i("img", {
                    directives: [{name: "lazy", rawName: "v-lazy", value: e.url, expression: "img.url"}],
                    staticClass: "lazy-load-img"
                })])
            }), t._v(" "), t.item.image_list.length < 4 ? i("a", {
                staticClass: "img-wrap img-item",
                attrs: {href: t.item.source_url, target: "_blank"}
            }, [i("span", {staticClass: "more-info"}, [t._v("\n        查看详情 "), i("tt-icon", {
                attrs: {
                    type: "nextpagetool",
                    color: "#406599",
                    size: "12"
                }
            })], 1)]) : t._e(), t._v(" "), t.item.ad_label ? t._e() : i("i", {staticClass: "pic-tip"}, [i("span", [t._v(t._s(t.item.gallary_image_count) + "图")])])], 2), t._v(" "), i("FooterBar", {attrs: {item: t.item}})], 1)
        }, staticRenderFns: []
    }
}, function (t, e) {
    t.exports = {
        render: function () {
            var t = this, e = t.$createElement, i = t._self._c || e;
            return i("div", {
                staticClass: "refresh-mode",
                attrs: {ga_event: "refresh_item_click"}
            }, [i("span", [t._v(t._s(t.item.time_ago) + "看到这里")]), t._v("\n   点击刷新 "), i("tt-icon", {
                attrs: {
                    type: "refresh",
                    size: "12",
                    color: "#2A90D7"
                }
            })], 1)
        }, staticRenderFns: []
    }
}, function (t, e) {
    t.exports = {
        render: function () {
            var t = this, e = t.$createElement, i = t._self._c || e;
            return i("pane-module", {attrs: {title: t.title}}, [i("template", {
                attrs: {slot: "content"},
                slot: "content"
            }, [i("ul", {staticClass: "friend-links-content"}, t._l(t.friendLink, function (e) {
                return i("li", {staticClass: "item"}, [i("a", {
                    attrs: {
                        target: "_blank",
                        href: e.url
                    }
                }, [t._v(t._s(e.name))])])
            }))])], 2)
        }, staticRenderFns: []
    }
}, , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , function (t, e) {
}, , function (t, e, i) {
    "use strict";

    function n(t) {
        return t && t.__esModule ? t : {default: t}
    }

    Object.defineProperty(e, "__esModule", {value: !0});
    var s = i(456), a = n(s);
    e.default = a.default
}, function (t, e, i) {
    "use strict";

    function n(t) {
        return t && t.__esModule ? t : {default: t}
    }

    Object.defineProperty(e, "__esModule", {value: !0});
    var s = i(458), a = n(s);
    e.default = a.default
}, function (t, e, i) {
    "use strict";

    function n(t) {
        return t && t.__esModule ? t : {default: t}
    }

    Object.defineProperty(e, "__esModule", {value: !0});
    var s = i(460), a = n(s);
    e.default = a.default
}, function (t, e) {
    "use strict";

    function i(t, e, i) {
        this.uploader = t, this.file = e, this.status = 0, this.progress = 0, this.uploadedBytes = 0, this.useChunk = i.useChunk || !1, this.chunkSize = i.chunkSize || 0, uploadUtils.isFile(e) ? this._createFile(e) : this._createFileFromInput(e.value), this.useChunk = this.useChunk && this.size > this.chunkSize
    }

    var n = 1, s = 2, a = 3, o = 4, r = 5;
    i.prototype._createFileFromInput = function (t) {
        this.lastModifiedDate = null, this.size = null, this.type = "file/" + t.slice(t.lastIndexOf(".") + 1).toLowerCase(), this.name = t.slice(t.lastIndexOf("/") + t.lastIndexOf("\\") + 2)
    }, i.prototype._createFile = function (t) {
        this.lastModifiedDate = t.lastModifiedDate, this.size = t.size, this.type = t.type, this.name = t.name
    }, i.prototype._slice = function (t, e) {
        var i = this.file.slice || this.file.mozSlice || this.file.webkitSlice;
        return i ? i.call(this.file, t, e, this.type) : this.file
    }, i.prototype.getChunkFile = function () {
        var t = this.uploadedBytes, e = t + this.chunkSize;
        return e > this.size && (e = this.size, this.chunkSize = e - this.uploadedBytes), {
            start: t,
            end: e,
            file: this._slice(t, e)
        }
    }, i.prototype.upload = function () {
        try {
            this.uploader.uploadItem(this)
        } catch (t) {
            throw t
        }
    }, i.prototype.cancel = function () {
        this.uploader.abortItem(this)
    }, i.prototype.onPrepareUpload = function () {
        this.status = n
    }, i.prototype.onBeforeUpload = function () {
        this.status = s
    }, i.prototype.onProgress = function (t, e) {
        this.status === s && (this.useChunk ? this.progress = Math.round((this.uploadedBytes + t) / this.size * 100) : this.progress = Math.round(t / e * 100))
    }, i.prototype.onAbort = function () {
        this.status = a
    }, i.prototype.onSuccess = function () {
        this.useChunk ? (this.uploadedBytes += this.chunkSize, this.uploadedBytes === this.size && (this.status = o)) : this.status = o
    }, i.prototype.onError = function () {
        this.status = r
    }, window.FileItem = i, t.exports = i
}, function (t, e, i) {
    "use strict";

    function n(t) {
        return t && t.__esModule ? t : {default: t}
    }

    var s = i(39), a = n(s), o = {
        isOnline: function () {
            return window.navigator.onLine
        }, isHTML5: function () {
            return !(!window.FormData || !File)
        }, extend: function (t) {
            var e = arguments.length;
            if (e < 2 || null == t) return t;
            for (var i = 1; i < e; i++) {
                var n = arguments[i];
                for (var s in n) Object.prototype.hasOwnProperty.call(n, s) && (t[s] = n[s])
            }
            return t
        }, isFile: function (t) {
            return !!(t instanceof File && (t.size >= 0 || t.type))
        }, isFileList: function (t) {
            return t instanceof FileList
        }, isArray: function (t) {
            return "[object Array]" === Object.prototype.toString.call(t)
        }, isEmptyObject: function (t) {
            if (this.isObject(t)) {
                var e = null;
                for (e in t) if (e) return !1
            }
            return !0
        }, isObject: function (t) {
            return null !== t && "object" === ("undefined" == typeof t ? "undefined" : (0, a.default)(t))
        }, toArray: function (t) {
            if (!t || 0 === t.length) return [];
            if (!t.length) return t;
            try {
                return [].slice.call(t)
            } catch (s) {
                for (var e = 0, i = t.length, n = []; e < i; e++) n.push(t[e]);
                return n
            }
        }
    };
    window.uploadUtils = o, t.exports = o
}, function (t, e, i) {
    "use strict";

    function n(t) {
        return t && t.__esModule ? t : {default: t}
    }

    Object.defineProperty(e, "__esModule", {value: !0});
    var s = i(463), a = n(s);
    i(316), e.default = a.default
}, , , function (t, e, i) {
    "use strict";

    function n(t) {
        return t && t.__esModule ? t : {default: t}
    }

    Object.defineProperty(e, "__esModule", {value: !0});
    var s = i(21), a = n(s), o = i(10), r = n(o), l = i(157), u = n(l), c = i(154), d = n(c), f = i(155), h = n(f),
        p = i(158), m = n(p), _ = i(457), v = n(_), g = i(156), w = n(g), y = i(153), b = n(y), C = i(63), k = n(C),
        x = i(64), S = i(37), M = i(6);
    e.default = {
        name: "FeedBox",
        mixins: [a.default],
        props: {
            category: {type: String, default: ""},
            url: {type: String, default: ""},
            qhAdSupport: {type: Boolean, default: !1},
            suspensionTip: {type: Boolean, default: !0},
            initList: {
                type: Array, default: function () {
                    return []
                }
            },
            containerCheck: {type: Boolean, default: !0},
            containerCheckCount: {type: Number, default: 8},
            dislikeUrl: {type: String, default: "/api/dislike/"},
            getUserInfoUrl: {type: String, default: "/user/info/"},
            onInit: {
                type: Function, default: function (t, e) {
                }
            },
            onLoadMore: {
                type: Function, default: function (t, e) {
                    window.ttAnalysis && window.ttAnalysis.send("event", {ev: "article_show_count", ext_id: e})
                }
            },
            onItemClick: {
                type: Function, default: function (t) {
                    t && t.ad_id && (0, x.ttSendMsg)({
                        label: "click",
                        value: t.ad_id,
                        extra: t.log_extra
                    }), t && t.ad_qihu_id > -1 && (0, S.qhSendMsg)(t.ad_qihu_id, "feed_qihu_ad", "click")
                }
            },
            onRefresh: {
                type: Function, default: function (t, e) {
                    window.ttAnalysis && window.ttAnalysis.send("event", {ev: "article_show_count", ext_id: e})
                }
            }
        },
        data: function () {
            return {refreshLock: !1, loadmoreLock: !1, loading: !1, offsetTop: 0, _feedList: null, list: []}
        },
        mounted: function () {
            this.configList(), this.$on("feed-item-dislike", this._dislike), this.$on("feed-refresh", this.refresh), r.default.$on("feed-refresh-bus", this.refresh), r.default.$on("feed-unshift-item", this.unshiftItem)
        },
        updated: function () {
            var t = document.querySelectorAll(".J_ad");
            t.length && (0, x.ttSetAds)(t);
            var e = document.querySelectorAll(".J_qihu_ad");
            e.length && (0, S.qhSetAds)(e, "feed_qihu_ad")
        },
        methods: {
            configList: function () {
                var t = this;
                this.offsetTop = (0, M.elOffset)(this.$refs.wrapper).top, this._feedList = new k.default({
                    url: this.url,
                    category: this.category,
                    qhAdSupport: this.qhAdSupport,
                    initList: this.initList
                }), 0 === this.initList.length ? this.refresh(!0) : this.list = this._feedList.getList(), this.onInit(this.list, this.list.length), setInterval(function () {
                    t.list = t._feedList.updateTime()
                }, 6e4)
            }, refresh: function (t) {
                var e = this;
                t === !0 && window.scrollTo(0, 0), t !== !0 && window.scrollTo(0, this.offsetTop), this.broadcast("MsgAlert", "feed-refresh", {}), this.loading = !0, this.refreshLock = !0, this._feedList.refresh(function (t, i) {
                    e.list = t, e.broadcast("MsgAlert", "feed-refresh-count", i), e.onRefresh(t, i)
                }, function () {
                    e.loading = !1, e.refreshLock = !1
                })
            }, unshiftItem: function (t) {
                var e = this;
                Array.isArray(t) && (this.loading = !0, this.refreshLock = !0, this._feedList.unshiftItem(t, function (t, i) {
                    e.list = t
                }, function () {
                    e.loading = !1, e.refreshLock = !1
                }))
            }, loadMore: function () {
                var t = this;
                this.loading = !0, this.loadmoreLock = !0, this._feedList.loadMore(function (e, i) {
                    t.list = e, t.onLoadMore(e, i)
                }, function () {
                    t.loading = !1, t.loadmoreLock = !1
                })
            }, feedItemClick: function (t) {
                this.onItemClick(t)
            }, _dislike: function (t) {
                this.list = this._feedList.dislikeItem(t)
            }
        },
        components: {
            singleMode: u.default,
            moreMode: d.default,
            noMode: h.default,
            ugcMode: m.default,
            refreshMode: w.default,
            videoMode: v.default,
            msgAlert: b.default
        }
    }
}, function (t, e, i) {
    "use strict";

    function n(t) {
        return t && t.__esModule ? t : {default: t}
    }

    Object.defineProperty(e, "__esModule", {value: !0});
    var s = i(36), a = n(s), o = i(6), r = i(10), l = n(r), u = i(243), c = n(u);
    e.default = {
        name: "VideoMode",
        data: function () {
            return {
                play: !1,
                destroyCount: 0,
                timer: null,
                smallSize: !1,
                dragOffset: {},
                mute: this.muteVolume,
                posterUrl: "",
                defaultImg: i(394)
            }
        },
        props: {
            item: {type: Object, default: {}},
            muteVolume: null,
            dislikeUrl: {type: String, default: "/api/dislike/"},
            getUserInfoUrl: {type: String, default: "/user/info/"}
        },
        filters: {
            formatCount: function (t) {
                return t ? t = (0, o.numFormat)(t) : "0"
            }
        },
        computed: {
            labels: function t() {
                var t = [];
                return this.item && this.item.label && this.item.label.length > 0 && (t = this.item.label.slice(0, 4)), t
            }
        },
        created: function () {
            this.item.image_url ? this.posterUrl = this.item.image_url.replace("190x124", "300x170") : this.posterUrl = this.defaultImg
        },
        mounted: function () {
            var t = this;
            l.default.$on("hover-poster", function (e) {
                e.group_id !== t.item.group_id && t.play && !t.smallSize && t.destroyPlayer()
            }), l.default.$on("minisize-player", function (e) {
                e.group_id !== t.item.group_id && t.smallSize && t.destroyPlayer(), t.smallSize || (t.mute = !0)
            }), l.default.$on("unset-volume", function (e) {
                t.mute = !1
            }), l.default.$on("pause-minisize-player", function (e) {
                e.group_id !== t.item.group_id && t.smallSize && (t.play = !1)
            })
        },
        methods: {
            playHandler: function () {
                this.play = !0, l.default.$emit("hover-poster", this.item), this.sendLog("video", "play", "click")
            }, enterHandler: function () {
                var t = this;
                return !this.smallSize && void(this.timer = setTimeout(function () {
                    t.play = !0, l.default.$emit("hover-poster", t.item), t.sendLog("video", "play", "hover")
                }, 500))
            }, leaveHandler: function () {
                clearTimeout(this.timer)
            }, fullSizeHandler: function () {
                this.sendLog("video", "fullsize", "click")
            }, zoomChange: function () {
                this.mute = !1, this.smallSize = !0, l.default.$emit("minisize-player", this.item), this.$emit("init-no-volume"), this.sendLog("video", "minisize", "click")
            }, zoomRecover: function () {
                this.destroyPlayer(), l.default.$emit("unset-volume"), this.$emit("init-has-volume")
            }, videoEndHandler: function () {
                this.smallSize && l.default.$emit("unset-volume")
            }, volumeChangeHandler: function (t) {
                t > 0 && (l.default.$emit("pause-minisize-player", this.item), l.default.$emit("unset-volume"), this.$emit("init-has-volume"))
            }, openDetail: function () {
                this.destroyPlayer()
            }, destroyPlayer: function () {
                this.smallSize = !1, this.play = !1, this.destroyCount += 1
            }, sendLog: function (t, e, i, n) {
                window._czc && _czc.push(["_trackEvent", t, e, i, n]), window.ttAnalysis && window.ttAnalysis.send("event", {ev: t + "_" + e + "_" + i})
            }
        },
        components: {Dislike: a.default, Player: c.default}
    }
}, function (t, e, i) {
    "use strict";

    function n(t) {
        return t && t.__esModule ? t : {default: t}
    }

    Object.defineProperty(e, "__esModule", {value: !0});
    var s = i(28), a = n(s), o = i(13), r = n(o), l = i(219), u = (n(l), i(10)), c = n(u), d = i(269), f = n(d),
        h = "__toutiao_player_volume__", p = "__toutiao_player_muted__";
    e.default = {
        props: {
            videoId: {type: String, required: !0},
            duration: String,
            siblings: Array,
            scrollMini: {type: Boolean, default: !1},
            posterUrl: String,
            theatreBtnVisible: {type: Boolean, default: !0},
            miniBtnVisible: {type: Boolean, default: !1},
            width: {type: Number, default: 660},
            height: {type: Number, default: 375},
            mute: Boolean,
            play: {type: Boolean, default: !1},
            destroyCount: {type: Number, default: 0}
        }, data: function () {
            return {
                id: "",
                isBeforeShowing: !1,
                isNextShowing: !1,
                isNextOneShowing: !1,
                isNextListShowing: !1,
                nextOneTitle: "",
                playNextTimer: null,
                isFloating: !1,
                countdownAddr: "//s3a.pstatp.com/site/tt_mfsroot/v-player/img/countdown.gif",
                moveParam: {},
                canMini: !1,
                showTransition: !1,
                theatreMode: !1,
                playerWidth: 660,
                playerHeight: 375
            }
        }, computed: {
            countdownAddrR: function () {
                return this.isNextOneShowing ? this.countdownAddr : ""
            }
        }, watch: {
            width: function (t) {
                this.playerWidth = t, this.vjsInstance && this.vjsInstance.dimensions(this.width, this.height)
            }, height: function (t) {
                this.theatreMode ? this.playerHeight = 478 : this.playerHeight = t, this.vjsInstance && this.vjsInstance.dimensions(this.width, this.height)
            }, play: function (t) {
                t === !0 ? this.startPlay() : this.vjsInstance && this.vjsInstance.pause()
            }, destroyCount: function () {
                this.vjsInstance && this.vjsInstance.dispose(), this.isBeforeShowing = !0, this.canMini = !1, this.isFloating = !1
            }, mute: function (t) {
                this.vjsInstance && this.isFloating && this.vjsInstance.muted(t)
            }
        }, methods: {
            startPlay: function () {
                var t = this;
                this.isFloating || (this.isBeforeShowing = !1, this.playerInstance = new f.default({
                    id: this.id,
                    vid: this.videoId,
                    autoplay: !0,
                    width: this.playerWidth,
                    height: this.playerHeight,
                    plugins: {vjs_definition: {}, vjs_zoom: {}, vjs_theatre: {}, vjs_hotkeys: {enableVolumeScroll: !1}}
                }), this.playerInstance.player_tt_resources().then(function () {
                    var e = t.playerInstance.play_list;
                    e.sort(function (t, e) {
                        return t.definition.replace(/[^\d]+/g, "") - e.definition.replace(/[^\d]+/g, "") < 0
                    }), e.length && t.playerInstance.play(e[0].src).then(function (i) {
                        t.vjsInstance = i;
                        var n = .65, s = !1;
                        if (window.localStorage) {
                            var a = localStorage.getItem(h), o = "true" === localStorage.getItem(p);
                            a && (n = a), o && (s = o)
                        }
                        void 0 !== t.mute && (s = t.mute), i.volume(n), i.muted(s), i.vjs_definition(e), t.$emit("video-play"), i.on("vjs_zoom_change", function (e, i) {
                            if (t.$emit("zoom-change"), window.localStorage) {
                                var n = window.localStorage.getItem("miniplayer-offset");
                                if (n) {
                                    n = JSON.parse(n);
                                    var s = t.$refs.playerWrap;
                                    s.style.left = n.left, s.style.top = n.top
                                }
                            }
                            t.isFloating = i, t.isBeforeShowing = !0
                        }), i.on("vjs_theatre_change", function (e, i) {
                            t.theatreMode = i, i ? t.playerHeight = 478 : t.playerHeight = t.height, t.vjsInstance && t.vjsInstance.dimensions(t.width, t.height), c.default.$emit("theater", i)
                        }), i.on("fullscreenchange", function () {
                            t.$emit("full-size")
                        }), i.on("volumechange", function () {
                            window.localStorage && (localStorage.setItem(h, i.volume()), localStorage.setItem(p, i.muted())), i.muted() !== !1 || t.isFloating || t.$emit("volume-change", i.volume())
                        }), i.on("ended", function () {
                            t.siblings && t.siblings.length > 0 && (t.isNextShowing = !0, t.isNextOneShowing = !0, t.playNextTimer = setTimeout(function () {
                                var e = t.siblings[0].link;
                                e ? window.location.href = e : window.location.reload()
                            }, 5e3)), t.$emit("video-end")
                        })
                    })
                }, function () {
                    t.playerInstance.player_error()
                }))
            }, cancel: function () {
                this.isNextOneShowing = !1, this.isNextListShowing = !0, clearTimeout(this.playNextTimer)
            }, replay: function () {
                this.isNextListShowing = !1, this.isNextShowing = !1, this.vjsInstance && this.vjsInstance.play()
            }, startDrag: function (t) {
                var e = this.moveParam;
                e.isDragging = !0, e.startX = t.clientX, e.startY = t.clientY;
                var i = this.$refs.playerWrap.getBoundingClientRect();
                e.startLeft = i.left, e.startTop = i.top, e.width = i.right - i.left, e.height = i.bottom - i.top, document.addEventListener("mousemove", this.mouseMove), document.addEventListener("mouseup", this.mouseUp)
            }, mouseMove: function (t) {
                var e = this.moveParam;
                if (e.isDragging) {
                    var i = t.clientX, n = t.clientY, s = window.innerWidth - document.body.clientWidth,
                        a = i - e.startX, o = n - e.startY, r = e.startLeft + a, l = e.startTop + o,
                        u = window.innerWidth || document.documentElement.clientWidth || document.body.clientWidth,
                        c = window.innerHeight || document.documentElement.clientHeight || document.body.clientHeight;
                    r < 0 ? r = 0 : r + e.width + s > u && (r = u - e.width - s), l < 0 ? l = 0 : l + e.height > c && (l = c - e.height);
                    var d = this.$refs.playerWrap;
                    d.style.left = r + "px", d.style.top = l + "px"
                }
            }, mouseUp: function () {
                var t = this.moveParam;
                if (t.isDragging && (document.getSelection && document.getSelection().removeAllRanges(), document.removeEventListener("mousemove", this.mouseMove), document.removeEventListener("mouseup", this.mouseUp), this.moveParam = {}), window.localStorage) {
                    var e = this.$refs.playerWrap;
                    window.localStorage.setItem("miniplayer-offset", (0, a.default)({
                        top: e.style.top,
                        left: e.style.left
                    }))
                }
            }, closeMini: function () {
                this.canMini = !1, this.isFloating = !1, this.$emit("close-mini")
            }
        }, created: function () {
            this.vjsInstance = null, this.playerInstance = null, this.id = "tt_video_" + Math.random().toString(16).slice(2, 7), this.canMini = this.scrollMini, this.playerWidth = this.width, this.playerHeight = this.height, this.posterUrl && (this.isBeforeShowing = !0), this.siblings && this.siblings.length > 0 && (this.nextOneTitle = this.siblings[0].title)
        }, mounted: function () {
            var t = this;
            this.posterUrl || this.startPlay(), window.addEventListener("scroll", (0, r.default)(function () {
                if (t.canMini && t.playerInstance) {
                    var e = t.$refs.player, i = e.getBoundingClientRect();
                    i.bottom < 0 ? (t.showTransition = !1, t.isFloating = !0) : t.isFloating = !1
                }
            }, 200)), window.addEventListener("resize", function () {
                t.showTransition = !0
            }), document.addEventListener("mouseover", function (e) {
                var i = document.querySelector(".vjs-theatre-btn");
                if (i) {
                    var n = i.getBoundingClientRect(), s = (n.left + n.right) / 2, a = (n.top + n.bottom) / 2,
                        o = e.clientX, r = e.clientY;
                    s - 50 < o && o < s + 50 && a - 50 < r && r < a + 50 && (t.showTransition = !0)
                }
            })
        }
    }
}, function (t, e, i) {
    "use strict";

    function n(t) {
        return t && t.__esModule ? t : {default: t}
    }

    Object.defineProperty(e, "__esModule", {value: !0});
    var s = i(28), a = n(s), o = i(3), r = n(o), l = i(25), u = (n(l), i(27)), c = n(u), d = i(10), f = n(d), h = i(69),
        p = n(h), m = i(223), _ = n(m);
    e.default = {
        name: "imgUploadBox", props: {}, data: function () {
            return {
                urlPrefix: "//www.toutiao.com/c/ugc/",
                cpLock: !1,
                uploadStatus: !0,
                submiting: !1,
                inputLength: 0,
                image_uris: [],
                inputContent: "",
                inputInvalid: !1,
                tooLong: !1,
                uploadFiles: [],
                uploader: null,
                isShowPopup: !1,
                options: {statusMsg: "", ctrMsg: "", isTranscoding: !1, imgList: [], loadingList: []},
                userData: {},
                msgTip: "",
                timer: null,
                isLongMode: !1,
                canSaveArticle: !!window.localStorage
            }
        }, computed: {
            uploadReady: function () {
                return "" !== this.inputContent && this.uploadStatus && !this.inputInvalid
            }, publishParams: function () {
                return {content: this.inputContent, image_uris: this.image_uris.join(",")}
            }
        }, mounted: function () {
            var t = this, e = this.uploader && this.uploader.getAll()[0];
            e && e.cancel(), f.default.$on("uploadReady", function (e) {
                t.uploadStatus = e.ready, e.uri && t.image_uris.push(e.uri)
            });
            var i = this.$refs.title;
            if (i.addEventListener("compositionstart", function () {
                t.cpLock = !0
            }), i.addEventListener("compositionend", function () {
                t.cpLock = !1
            }), (0, c.default)({
                successCb: function (e) {
                    t.userData = e
                }
            }), this.canSaveArticle) {
                var n = window.localStorage.getItem("tt_ugc_article");
                n && (n = JSON.parse(n), this.inputContent = n.article, this.inputLength = this.inputContent.length,
                    this.isLongMode = n.isLongMode)
            }
        }, methods: {
            saveArticle: function () {
                window.localStorage.setItem("tt_ugc_article", (0, a.default)({
                    article: this.inputContent,
                    isLongMode: this.isLongMode
                })), this.$Toast({message: "保存成功！"})
            }, longMode: function () {
                this.isLongMode = !this.isLongMode, this.$emit("onLongMode", this.isLongMode)
            }, toast: function (t) {
                var e = this;
                this.timer && (clearTimeout(this.timer), this.timer = null), this.msgTip = t.message, this.timer = setTimeout(function () {
                    e.msgTip = "", clearTimeout(e.timer), e.timer = null
                }, 3e3)
            }, togglePopup: function () {
                this.isShowPopup = !this.isShowPopup
            }, clearPopup: function (t) {
                return !(t && this.options.imgList.length && !window.confirm("确定要放弃上传图片？")) && (this.isShowPopup = !1, this.options.imgList = [], void(this.image_uris = []))
            }, removeImg: function (t, e) {
                this.image_uris.splice(e, 1), this.options.imgList = (0, _.default)(this.options.imgList, t)
            }, uploadActionClick: function () {
                this.$refs.fileElem.click()
            }, handleFile: function () {
                if (this.uploadFiles = this.$refs.fileElem.files, this.uploadFiles.length > 0 && this.uploadFiles.length <= 9 - this.options.imgList.length) {
                    f.default.$emit("uploadReady", {ready: !1, type: "img"}), f.default.$emit("uploadPending");
                    for (var t = this.uploadFiles.length - 1; t >= 0; t--) this.options.loadingList.push(t);
                    this._initUploader()
                } else this.toast({message: this.uploadFiles.length > 0 ? "上传的图片过多" : "请选择正确的图片"})
            }, _initUploader: function () {
                var t = this;
                this.uploader = new p.default({
                    url: this.urlPrefix + "image/upload/",
                    filters: [{
                        name: "acceptType", fn: function (t) {
                            var e = /image/;
                            return e.test(t.type)
                        }, fail: function () {
                            t.toast({message: "文件类型错误"}), t.options.loadingList.pop()
                        }
                    }, {
                        name: "size", fn: function (t) {
                            var e = t.size / 1048576;
                            return e < 10
                        }, fail: function () {
                            t.toast({message: "图片大小超过10M，请更换图片"}), t.options.loadingList.pop()
                        }
                    }],
                    progressCbk: function (t) {
                    },
                    completeCbk: function (e) {
                        t.options.loadingList.pop(), e && e.responseItem && e.responseItem.data && e.responseItem.data.url_list && (t.options.imgList.push(e.responseItem.data.url_list[0].url), f.default.$emit("uploadReady", {
                            ready: !0,
                            type: "img",
                            uri: e.responseItem.data.web_uri
                        }), t.$refs.fileElemHolder.reset()), window.ttAnalysis && window.ttAnalysis.send("event", {ev: "user_ugc_img_complete"})
                    },
                    errorCbk: function (e) {
                        t.toast({message: "上传失败，请更换图片再试试"}), f.default.$emit("uploadReady", {ready: !0, type: "img"})
                    }
                });
                var e = this.uploader.addToQueue(this.uploadFiles);
                e ? this.uploader.uploadAll() : this.$refs.fileElemHolder.reset()
            }, inputKeyup: function (t) {
                return !this.cpLock && (this.inputLength = t.length, void(this.inputLength > 2e3 ? (this.inputInvalid = !0, this.tooLong = !0) : (this.inputInvalid = !1, this.tooLong = !1)))
            }, publishImg: function () {
                var t = this;
                return "" == this.inputContent ? (this.toast({message: "请输入内容"}), !1) : !(!this.uploadReady || this.submiting) && (this.image_uris.length > 9 ? (this.toast({message: "图片不能多于 9 张"}), !1) : (this.submiting = !0, void(0, r.default)({
                    url: this.urlPrefix + "content/publish/",
                    method: "post",
                    data: this.publishParams,
                    success: function (e) {
                        "success" === e.message ? (t.$Toast({message: "发布成功！"}), f.default.$emit("feed-unshift-item", [{
                            ugc_data: {
                                content: t.publishParams.content,
                                comment_count: 0,
                                digg_count: 0,
                                read_count: 0,
                                ugc_images: t.options.imgList,
                                ugc_user: {
                                    open_url: "/c/user/" + t.userData.user_id + "/",
                                    avatar_url: t.userData.avatar_url,
                                    name: t.userData.name,
                                    user_verified: 0,
                                    is_following: !0,
                                    user_auth_info: {auth_info: ""}
                                }
                            },
                            behot_time: Math.floor((new Date).getTime() / 1e3),
                            source_url: e.data.open_url,
                            group_id: "" + e.data.group_id
                        }]), t.submiting = !1, t.clearPopup(), t.inputContent = "", t.inputLength = 0, f.default.$emit("publishSuccess"), window.ttAnalysis && window.ttAnalysis.send("event", {ev: "user_ugc_img_submit"}), window.localStorage.removeItem("tt_ugc_article")) : "error" == e.message && (t.toast({message: e.data || "发布失败，请重试"}), t.submiting = !1, f.default.$emit("publishFail"))
                    },
                    error: function (e) {
                        t.toast({message: e.data || "服务器开了点小差，请稍后再试"}), t.submiting = !1, f.default.$emit("publishFail")
                    }
                })))
            }
        }
    }
}, function (t, e, i) {
    "use strict";

    function n(t) {
        return t && t.__esModule ? t : {default: t}
    }

    Object.defineProperty(e, "__esModule", {value: !0});
    var s = i(3), a = (n(s), i(25)), o = (n(a), i(10)), r = n(o), l = i(459), u = n(l), c = i(461), d = n(c),
        f = i(462), h = n(f);
    e.default = {
        name: "Publisher",
        props: {showPublisher: {type: Boolean, default: !0}, type: {type: Number, default: 1}},
        data: function () {
            return {
                showType: this.type,
                isLongMode: !1,
                options: {isPending: !1},
                typeCn: {1: "图文", 2: "视频", 3: "提问"},
                typeLog: {1: "img_tab_click", 2: "video_tab_click", 3: "wenda_tab_click"}
            }
        },
        watch: {
            type: function (t) {
                this.showType = t
            }
        },
        mounted: function () {
            var t = this;
            r.default.$on("uploadPending", function () {
                t.options.isPending = !0
            }), r.default.$on("publishSuccess", function () {
                t.options.isPending = !1
            }), window.onbeforeunload = function () {
                if (t.options.isPending) return "您有一个未完成的任务，关闭或刷新页面会导致数据丢失"
            }
        },
        methods: {
            changeType: function (t) {
                var e = this.typeCn[this.showType], i = this.typeLog[t];
                return !(this.options.isPending && !window.confirm("是否放弃" + e + "发布？")) && (this.options.isPending = !1, this.showType = t || 1, void(window.ttAnalysis && window.ttAnalysis.send("event", {ev: i})))
            }, longModeHandler: function (t) {
                this.isLongMode = t
            }
        },
        components: {imgUploadBox: u.default, videoUploadBox: d.default, wendaUploadBox: h.default}
    }
}, function (t, e, i) {
    "use strict";

    function n(t) {
        return t && t.__esModule ? t : {default: t}
    }

    Object.defineProperty(e, "__esModule", {value: !0});
    var s = i(3), a = n(s), o = i(27), r = n(o), l = i(10), u = n(l), c = i(69), d = n(c);
    e.default = {
        name: "videoUploadBox", props: {}, data: function () {
            return {
                urlPrefix: "//www.toutiao.com/c/ugc/",
                cpLock: !1,
                isShowPopup: !1,
                inputContent: "",
                uploadStatus: !1,
                submiting: !1,
                inputLength: 0,
                publishParams: {video_id: "", thumb_uri: "", thumb_source: 1, title: ""},
                inputInvalid: !1,
                tooLong: !1,
                fileStatus: {1: "准备中...", 2: "上传中", 3: "上传取消", 4: "上传成功", 5: "上传失败"},
                fileCtrStatus: {1: "", 2: "取消上传", 3: "", 4: "重新上传", 5: "继续上传"},
                fileSugStatus: {1: "", 2: "", 3: "", 4: "", 5: "视频无法上传，请检查网络环境"},
                _files: [],
                _uploader: null,
                _uploadId: 0,
                _status: 0,
                options: {
                    isInitShow: !0,
                    title: "",
                    statusMsg: "准备中...",
                    ctrMsg: "",
                    sugMsg: "",
                    progress: 0,
                    poster: "",
                    isTranscoding: !1
                },
                msgTip: "",
                timer: null
            }
        }, computed: {
            uploadReady: function () {
                return "" !== this.inputContent && this.uploadStatus && !this.inputInvalid
            }
        }, mounted: function () {
            var t = this;
            u.default.$on("uploadReady", function (e) {
                t.uploadStatus = e.ready, t.publishParams.video_id = e.video_id
            });
            var e = this.$refs.title;
            e.addEventListener("compositionstart", function () {
                t.cpLock = !0
            }), e.addEventListener("compositionend", function () {
                t.cpLock = !1
            }), (0, r.default)({
                successCb: function (e) {
                    t.userData = e
                }
            })
        }, methods: {
            toast: function (t) {
                var e = this;
                this.timer && (clearTimeout(this.timer), this.timer = null), this.msgTip = t.message, this.timer = setTimeout(function () {
                    e.msgTip = "", clearTimeout(e.timer), e.timer = null
                }, 3e3)
            }, togglePopup: function () {
                this.isShowPopup = !this.isShowPopup
            }, clearPopup: function (t) {
                if (t && this._uploader && !window.confirm("确定要放弃上传视频？")) return !1;
                if (this.isShowPopup = !1, this._uploader) {
                    var e = this._uploader.getAll()[0];
                    e.cancel()
                }
                this.options.isInitShow = !0, this.options.poster = "", this.uploadStatus = !1, this.publishParams = {
                    video_id: "",
                    title: "",
                    thumb_uri: "",
                    thumb_source: 1
                }
            }, uploadActionClick: function () {
                this.$refs.fileElem.click()
            }, handleFile: function () {
                this.options.isInitShow = !1, this._files = this.$refs.fileElem.files, this._files.length && (this.options.title = this._files[0].name, u.default.$emit("uploadReady", {
                    ready: !1,
                    type: "video"
                }), this.options.poster = "", u.default.$emit("uploadPending"), this._beginUpload())
            }, onCtrClick: function () {
                var t = this._uploader.getAll()[0];
                return !!t && void(2 === this._status ? window.confirm("确定取消咩") && (t.cancel(), this.$refs.fileElemHolder.reset(), this.options.isInitShow = !0) : 4 === this._status ? (this.$refs.fileElemHolder.reset(), this.$refs.fileElem.click()) : 5 === this._status && this._uploader.uploadContinue(t))
            }, _beginUpload: function () {
                var t = this;
                (0, a.default)({
                    url: this.urlPrefix + "video/upload/", method: "get", success: function (e) {
                        if ("success" === e.message) {
                            var i = e.data;
                            t._uploadId = i.upload_id, t._initUploader({url: i.upload_url, chunkSize: i.chunk_size})
                        }
                    }
                })
            }, _initUploader: function (t) {
                var e = this;
                this._uploader = new d.default({
                    url: t.url,
                    chunk_size: t.chunk_size,
                    filters: [{
                        name: "acceptType", fn: function (t) {
                            var e = /video/;
                            return e.test(t.type)
                        }, fail: function () {
                            e.toast({message: "文件类型错误"})
                        }
                    }, {
                        name: "size", fn: function (t) {
                            var e = t.size / 1048576;
                            return e < 250
                        }, fail: function () {
                            e.toast({message: "文件过大"})
                        }
                    }],
                    progressCbk: function (t) {
                        e._updateFileStatus(2), e.options.progress = t.progress
                    },
                    completeCbk: function (t) {
                        t.responseItem && (e.publishParams.thumb_uri = t.responseItem.poster_uri || "", e.options.poster = t.responseItem.poster_url || ""), e._updateFileStatus(t.status)
                    }
                });
                var i = this._uploader.addToQueue(this._files);
                i ? this._uploader.uploadAll() : this.options.isInitShow = !0
            }, _updateFileStatus: function (t) {
                this._status = t, this._updateMsgs(t), this._getFileInfo(t)
            }, _updateMsgs: function (t) {
                this.options.statusMsg = this.fileStatus[t], this.options.ctrMsg = this.fileCtrStatus[t], this.options.sugMsg = this.fileSugStatus[t]
            }, _getFileInfo: function (t) {
                return 4 === t && (u.default.$emit("uploadReady", {
                    ready: !0,
                    type: "video",
                    video_id: this._uploadId
                }), void(window.ttAnalysis && window.ttAnalysis.send("event", {ev: "user_ugc_video_complete"})))
            }, inputKeyup: function (t) {
                return !this.cpLock && (this.inputLength = t.length, void(this.inputLength > 30 ? (this.inputInvalid = !0, this.tooLong = !0) : (this.inputInvalid = !1, this.tooLong = !1)))
            }, publishVideo: function () {
                var t = this;
                return "" == this.inputContent ? (this.toast({message: "请输入内容"}), !1) : this.publishParams.video_id ? !(!this.uploadReady || this.submiting) && (this.submiting = !0, this.publishParams.title = this.inputContent, void(0, a.default)({
                    url: this.urlPrefix + "video/publish/",
                    method: "POST",
                    data: this.publishParams,
                    success: function (e) {
                        "success" === e.message ? (t.$Toast({message: "发布成功，转码中，请稍后在个人主页中查看"}), t.submiting = !1, t.clearPopup(), t.inputContent = "", t.inputLength = 0, u.default.$emit("publishSuccess"), window.ttAnalysis && window.ttAnalysis.send("event", {ev: "user_ugc_video_submit"})) : "error" == e.message && (t.toast({message: e.data || "发布失败，请重试"}), t.submiting = !1, u.default.$emit("publishFail"))
                    },
                    error: function (e) {
                        t.toast({message: e.data || "服务器开了点小差，请稍后再试"}), t.submiting = !1, u.default.$emit("publishFail")
                    }
                })) : (this.toast({message: "请上传视频"}), !1)
            }
        }
    }
}, function (t, e, i) {
    "use strict";

    function n(t) {
        return t && t.__esModule ? t : {default: t}
    }

    Object.defineProperty(e, "__esModule", {value: !0});
    var s = i(3), a = n(s), o = i(25), r = (n(o), i(27)), l = n(r), u = i(10), c = n(u), d = i(69), f = n(d),
        h = i(223), p = n(h);
    e.default = {
        name: "wendaUploadBox", props: {}, data: function () {
            return {
                urlPrefix: "//www.toutiao.com/c/ugc/",
                cpLock: !1,
                uploadStatus: !1,
                submiting: !1,
                inputLength: 0,
                image_uris: [],
                inputTitle: "",
                inputContent: "",
                contentInvalid: !1,
                titleInvalid: !1,
                uploadFiles: [],
                uploader: null,
                isShowPopup: !1,
                options: {statusMsg: "", ctrMsg: "", isTranscoding: !1, imgList: [], loadingList: []},
                userData: {},
                msgTip: "",
                timer: null
            }
        }, computed: {
            uploadReady: function () {
                return this.inputTitle.length > 4 && this.inputTitle.length < 40 && !this.contentInvalid
            }, publishParams: function () {
                return {
                    title: this.inputTitle,
                    content: this.inputContent,
                    pic_list: this.image_uris,
                    t: (new Date).getTime(),
                    enter_from: "direct_toutiao"
                }
            }
        }, mounted: function () {
            var t = this, e = this.uploader && this.uploader.getAll()[0];
            e && e.cancel(), c.default.$on("uploadReady", function (e) {
                t.uploadStatus = e.ready, e.uri && t.image_uris.push(e.uri)
            });
            var i = this.$refs.title, n = this.$refs.content;
            i.addEventListener("compositionstart", function () {
                t.cpLock = !0
            }), i.addEventListener("compositionend", function () {
                t.cpLock = !1
            }), n.addEventListener("compositionstart", function () {
                t.cpLock = !0
            }), n.addEventListener("compositionend", function () {
                t.cpLock = !1
            }), (0, l.default)({
                successCb: function (e) {
                    t.userData = e
                }
            })
        }, methods: {
            toast: function (t) {
                var e = this;
                this.timer && (clearTimeout(this.timer), this.timer = null), this.msgTip = t.message, this.timer = setTimeout(function () {
                    e.msgTip = "", clearTimeout(e.timer), e.timer = null
                }, 3e3)
            }, togglePopup: function () {
                this.isShowPopup = !this.isShowPopup
            }, clearPopup: function (t) {
                return !(t && this.options.imgList.length && !window.confirm("确定要放弃上传图片？")) && (this.isShowPopup = !1, this.options.imgList = [], void(this.image_uris = []))
            }, removeImg: function (t, e) {
                this.image_uris.splice(e, 1), this.options.imgList = (0, p.default)(this.options.imgList, t)
            }, uploadActionClick: function () {
                this.$refs.fileElem.click()
            }, handleFile: function () {
                if (this.uploadFiles = this.$refs.fileElem.files, this.uploadFiles.length > 0 && this.uploadFiles.length <= 3 - this.options.imgList.length) {
                    c.default.$emit("uploadReady", {ready: !1, type: "img"}), c.default.$emit("uploadPending");
                    for (var t = this.uploadFiles.length - 1; t >= 0; t--) this.options.loadingList.push(t);
                    this._initUploader()
                } else this.$Toast({message: this.uploadFiles.length > 0 ? "上传的图片过多" : "请选择正确的图片"})
            }, _initUploader: function () {
                var t = this;
                this.uploader = new f.default({
                    url: this.urlPrefix + "image/upload/",
                    filters: [{
                        name: "acceptType", fn: function (t) {
                            var e = /image/;
                            return e.test(t.type)
                        }, fail: function () {
                            t.$Toast({message: "文件类型错误"}), t.options.loadingList.pop()
                        }
                    }, {
                        name: "size", fn: function (t) {
                            var e = t.size / 1048576;
                            return e < 10
                        }, fail: function () {
                            t.$Toast({message: "图片大小超过10M，请更换图片"}), t.options.loadingList.pop()
                        }
                    }],
                    progressCbk: function (t) {
                    },
                    completeCbk: function (e) {
                        t.options.loadingList.pop(), e && e.responseItem && e.responseItem.data && e.responseItem.data.url_list && (t.options.imgList.push(e.responseItem.data.url_list[0].url), c.default.$emit("uploadReady", {
                            ready: !0,
                            type: "img",
                            uri: e.responseItem.data.web_uri
                        }), t.$refs.fileElemHolder.reset())
                    },
                    errorCbk: function (e) {
                        t.$Toast({message: "上传失败，请更换图片再试试"}), c.default.$emit("uploadReady", {ready: !0, type: "img"})
                    }
                });
                var e = this.uploader.addToQueue(this.uploadFiles);
                e ? this.uploader.uploadAll() : this.$refs.fileElemHolder.reset()
            }, contentKeyup: function (t) {
                return !this.cpLock && (this.inputLength = t.length, void(this.inputLength > 40 ? this.contentInvalid = !0 : this.contentInvalid = !1))
            }, publishImg: function () {
                var t = this;
                return this.inputTitle.length < 4 ? (this.toast({message: "标题不得少于 4 字"}), !1) : this.inputTitle.length > 40 ? (this.toast({message: "标题不得多于 40 字"}), !1) : !(!this.uploadReady || this.submiting) && (this.image_uris.length > 3 ? (this.$Toast({message: "图片不能多于 3 张"}), !1) : (this.submiting = !0, void(0, a.default)({
                    url: "//www.toutiao.com/wenda/web/commit/postquestion/",
                    method: "post",
                    data: this.publishParams,
                    success: function (e) {
                        0 === e.err_no ? (t.$Toast({message: "发布成功！"}), c.default.$emit("feed-unshift-item", [{
                            title: t.publishParams.title,
                            single_mode: !!t.options.imgList.length,
                            article_genre: "wenda",
                            source: "悟空问答",
                            image_url: t.options.imgList[0],
                            behot_time: Math.floor((new Date).getTime() / 1e3),
                            source_url: "//www.wukong.com/question/" + e.qid + "/"
                        }]), t.submiting = !1, t.clearPopup(), t.inputContent = "", t.inputTitle = "", t.inputLength = 0, c.default.$emit("publishSuccess"), window.ttAnalysis && window.ttAnalysis.send("event", {ev: "user_ugc_wenda_submit"})) : (t.$Toast({message: e.data || "发布失败，请重试"}), t.submiting = !1, c.default.$emit("publishFail"))
                    },
                    error: function (t) {
                        this.$Toast({message: t.data || "服务器开了点小差，请稍后再试"}), this.submiting = !1, c.default.$emit("publishFail")
                    }
                })))
            }
        }
    }
}, function (t, e, i) {
    "use strict";

    function n(t) {
        return t && t.__esModule ? t : {default: t}
    }

    Object.defineProperty(e, "__esModule", {value: !0});
    var s = i(465), a = n(s), o = i(464), r = n(o);
    e.default = {name: "is-login", props: {loginInfo: {}}, components: {Login: a.default, Logged: r.default}}
}, function (t, e) {
    "use strict";
    Object.defineProperty(e, "__esModule", {value: !0}), e.default = {
        name: "logged",
        props: {loginInfo: {}},
        methods: {
            numFormat: function (t) {
                return t < 1e5 ? t : t < 1e8 ? (t / 1e4).toFixed(1) + "万" : (t / 1e8).toFixed(1) + "亿"
            }
        },
        computed: {
            following: function () {
                return this.numFormat(this.loginInfo.following)
            }, fans: function () {
                return this.numFormat(this.loginInfo.fans)
            }
        }
    }
}, function (t, e) {
    "use strict";
    Object.defineProperty(e, "__esModule", {value: !0}), e.default = {
        name: "login", methods: {
            snsLogin: function (t) {
                var e = t.target || t.srcElement;
                if ("li" === e.tagName.toLowerCase()) {
                    var i = e.getAttribute("data-pid");
                    window.Slardar && window.Slardar.sendCustomCountLog("sns_login", i), this._snsLogin(i)
                }
            }, _snsLogin: function (t) {
                var e = "https://www.toutiao.com/auth/connect/?type=sso";
                e += "&platform=" + t, e += "&next=https://sso.toutiao.com/auth/login_success/?service=https://www.toutiao.com/", window.location.href = e
            }
        }
    }
}, , , function (t, e, i) {
    "use strict";

    function n(t) {
        return t && t.__esModule ? t : {default: t}
    }

    Object.defineProperty(e, "__esModule", {value: !0});
    var s = i(10), a = n(s), o = i(6), r = i(13), l = n(r), u = i(66), c = n(u), d = i(244), f = n(d), h = i(242),
        p = n(h), m = i(38), _ = n(m), v = i(70), g = n(v), w = i(77), y = n(w), b = i(68), C = n(b), k = i(67),
        x = n(k), S = i(78), M = n(S), L = i(76), I = n(L), P = i(75), T = n(P), E = i(79), $ = n(E), F = i(74),
        A = n(F), j = i(247), U = n(j), H = window.BASE_DATA || {}, z = window.PAGE_SWITCH || {};
    e.default = {
        name: "app",
        data: function () {
            return {
                category: H.category,
                url: "/api/pc/feed/",
                qhAdSupport: z.qihuAdShow || !1,
                headerInfo: H.headerInfo,
                tag: H.tag,
                newsHover: !1,
                isShowNewsImg: !1,
                province: H.province,
                isLogin: H.isLogin,
                loginInfo: H.loginInfo
            }
        },
        methods: {
            refreshFeed: function () {
                a.default.$emit("feed-refresh-bus")
            }
        },
        mounted: function () {
            var t = this, e = document.getElementById("rightModule"), i = (0, o.elOffset)(e).top, n = 0, s = 0;
            window.addEventListener("scroll", (0, l.default)(function () {
                n = e.clientHeight, s = (0, o.getScrollTop)(window), i + n > s ? (t.newsHover = !1, t.isShowNewsImg = !1) : (t.newsHover = !0, t.isShowNewsImg = !0)
            }, 60), !1)
        },
        components: {
            TtHeader: c.default,
            NewsSlide: $.default,
            FeedBox: p.default,
            Channel: A.default,
            SearchBox: _.default,
            HotNews: y.default,
            HotVideos: C.default,
            HotImages: x.default,
            MoreLinks: M.default,
            FriendLinks: I.default,
            Company: T.default,
            Toolbar: g.default,
            Publisher: f.default,
            IsLogin: U.default
        }
    }
}, , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , function (t, e) {
}, function (t, e) {
}, function (t, e) {
}, function (t, e) {
}, function (t, e) {
}, function (t, e) {
}, function (t, e) {
}, , function (t, e) {
}, , function (t, e) {
}, , function (t, e) {
}, function (t, e) {
}, function (t, e) {
}, , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , function (t, e, i) {
    t.exports = i.p + "static/img/e8e8e8.b6c0f54.png"
}, , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , function (t, e, i) {
    i(317);
    var n = i(1)(i(250), i(469), null, null);
    t.exports = n.exports
}, function (t, e, i) {
    i(329);
    var n = i(1)(i(251), i(481), null, null);
    t.exports = n.exports
}, function (t, e, i) {
    i(320);
    var n = i(1)(i(252), i(472), null, null);
    t.exports = n.exports
}, function (t, e, i) {
    i(319);
    var n = i(1)(i(253), i(471), null, null);
    t.exports = n.exports
}, function (t, e, i) {
    i(318);
    var n = i(1)(i(254), i(470), null, null);
    t.exports = n.exports
}, function (t, e, i) {
    i(322);
    var n = i(1)(i(255), i(474), null, null);
    t.exports = n.exports
}, function (t, e, i) {
    i(321);
    var n = i(1)(i(256), i(473), null, null);
    t.exports = n.exports
}, function (t, e, i) {
    i(326);
    var n = i(1)(i(257), i(478), "data-v-51591f6c", null);
    t.exports = n.exports
}, function (t, e, i) {
    i(330);
    var n = i(1)(i(258), i(482), "data-v-e2122872", null);
    t.exports = n.exports
}, function (t, e, i) {
    i(324);
    var n = i(1)(i(259), i(476), "data-v-4a7e2734", null);
    t.exports = n.exports
}, , , function (t, e, i) {
    i(328);
    var n = i(1)(i(262), i(480), null, null);
    t.exports = n.exports
}, function (t, e) {
    t.exports = {
        render: function () {
            var t = this, e = t.$createElement, i = t._self._c || e;
            return i("div", {
                ref: "wrapper",
                staticClass: "feed-infinite-wrapper"
            }, [i("tt-loading", {
                directives: [{
                    name: "show",
                    rawName: "v-show",
                    value: t.refreshLock,
                    expression: "refreshLock"
                }], attrs: {message: "推荐中"}
            }), t._v(" "), i("msgAlert", {
                attrs: {
                    category: t.category,
                    suspensionTip: t.suspensionTip
                }
            }), t._v(" "), i("ul", {
                directives: [{
                    name: "infinite-scroll",
                    rawName: "v-infinite-scroll",
                    value: t.loadMore,
                    expression: "loadMore"
                }],
                attrs: {
                    "infinite-scroll-disabled": "loading",
                    "infinite-scroll-immediate-check": "containerCheck",
                    "infinite-scroll-immediate-check-count": "containerCheckCount",
                    "infinite-scroll-distance": "80"
                }
            }, t._l(t.list, function (e, n) {
                return i("li", {
                    key: e.group_id,
                    class: {J_ad: e.ad_id, J_qihu_ad: e.ad_qihu_id > -1},
                    attrs: {ad_id: e.ad_id, ad_qihu_id: e.ad_qihu_id, ad_extra: e.log_extra, ad_track: e.ad_track},
                    on: {
                        click: function (i) {
                            t.feedItemClick(e)
                        }
                    }
                }, [e.ugc_data ? i("ugcMode", {attrs: {item: e}}) : e.has_video && "ad" !== e.article_genre ? i("videoMode", {
                    attrs: {
                        item: e,
                        "dislike-url": t.dislikeUrl,
                        "getUser-info-url": t.getUserInfoUrl
                    }
                }) : e.single_mode ? i("singleMode", {
                    attrs: {
                        item: e,
                        dislikeUrl: t.dislikeUrl,
                        getUserInfoUrl: t.getUserInfoUrl
                    }
                }) : e.has_gallery ? i("moreMode", {attrs: {item: e}}) : e.refresh_mode ? i("refreshMode", {
                    attrs: {item: e},
                    nativeOn: {
                        click: function (e) {
                            e.stopPropagation(), t.refresh(e)
                        }
                    }
                }) : i("noMode", {attrs: {item: e}})], 1)
            })), t._v(" "), i("tt-loading", {
                directives: [{
                    name: "show",
                    rawName: "v-show",
                    value: t.loadmoreLock,
                    expression: "loadmoreLock"
                }], attrs: {message: "加载中"}
            })], 1)
        }, staticRenderFns: []
    }
}, function (t, e) {
    t.exports = {
        render: function () {
            var t = this, e = t.$createElement, i = t._self._c || e;
            return t.showPublisher ? i("div", {staticClass: "ugcBox"}, [i("div", {staticClass: "ugcBox-inner"}, [i("ul", {staticClass: "bui-box ugc-tab-list"}, [i("li", {
                staticClass: "bui-left ugc-tab-item",
                class: {current: 1 === t.showType},
                on: {
                    click: function (e) {
                        t.changeType(1)
                    }
                }
            }, [t._v("发布图文")]), t._v(" "), i("li", {
                staticClass: "bui-left ugc-tab-item",
                class: {current: 2 === t.showType},
                on: {
                    click: function (e) {
                        t.changeType(2)
                    }
                }
            }, [t._v("发布视频")]), t._v(" "), i("li", {
                staticClass: "bui-left ugc-tab-item",
                class: {current: 3 === t.showType},
                on: {
                    click: function (e) {
                        t.changeType(3)
                    }
                }
            }, [t._v("发布问答")])]), t._v(" "), i("div", {staticClass: "ugc-content"}, [1 === t.showType ? i("div", [i("imgUploadBox", {on: {onLongMode: t.longModeHandler}})], 1) : t._e(), t._v(" "), 2 === t.showType ? i("div", [i("videoUploadBox")], 1) : t._e(), t._v(" "), 3 === t.showType ? i("div", [i("wendaUploadBox")], 1) : t._e()]), t._v(" "), t.isLongMode ? i("a", {
                staticClass: "pgc-inviter",
                attrs: {href: "//mp.toutiao.com/profile_register/register-type", target: "_blank"}
            }, [t._v("想更加专业的发布文章？点击这里加入头条号")]) : t._e()])]) : t._e()
        }, staticRenderFns: []
    }
}, function (t, e) {
    t.exports = {
        render: function () {
            var t = this, e = t.$createElement, i = t._self._c || e;
            return i("div", {staticClass: "imgBox upload-box"}, [i("textarea", {
                directives: [{
                    name: "model",
                    rawName: "v-model.trim",
                    value: t.inputContent,
                    expression: "inputContent",
                    modifiers: {trim: !0}
                }],
                ref: "title",
                staticClass: "title-input",
                class: {warning: t.inputInvalid, long: t.isLongMode},
                attrs: {placeholder: "有什么新鲜事想告诉大家"},
                domProps: {value: t.inputContent},
                on: {
                    keyup: function (e) {
                        t.inputKeyup(e.target.value)
                    }, input: function (e) {
                        e.target.composing || (t.inputContent = e.target.value.trim())
                    }, blur: function (e) {
                        t.$forceUpdate()
                    }
                }
            }), t._v(" "), i("p", {staticClass: "words-number"}, [t._v(t._s(t.inputLength) + "/2000字")]), t._v(" "), i("div", {staticClass: "bui-box upload-footer"}, [i("div", {staticClass: "bui-left"}, [i("span", {
                staticClass: "show-image-uploader show-uploader",
                attrs: {ga_event: "user_ugc_img_open"},
                on: {click: t.togglePopup}
            }, [i("tt-icon", {
                attrs: {
                    type: "pic_tool",
                    size: "20",
                    color: "#ed4040"
                }
            }), t._v(" "), i("span", [t._v("添加图片")])], 1), t._v(" "), i("span", {
                staticClass: "checkbox-wrap",
                on: {click: t.longMode}
            }, [i("span", {
                staticClass: "checkbox",
                class: {checked: t.isLongMode}
            }, [i("tt-icon", {
                attrs: {
                    type: "check",
                    size: "10",
                    color: "#fff"
                }
            })], 1), t._v(" "), i("span", [t._v("发布长文")])])]), t._v(" "), i("div", {staticClass: "bui-right"}, [i("span", {staticClass: "msg-tip"}, [t._v(t._s(t.msgTip))]), t._v(" "), i("a", {
                staticClass: "upload-publish",
                class: {active: t.uploadReady},
                on: {click: t.publishImg}
            }, [t._v("发布")])])]), t._v(" "), i("div", {
                directives: [{
                    name: "show",
                    rawName: "v-show",
                    value: t.isShowPopup,
                    expression: "isShowPopup"
                }], staticClass: "uploader-popup"
            }, [i("div", {staticClass: "imgUploadBox"}, [i("p", {staticClass: "uploader-title"}, [t._v("本地上传")]), t._v(" "), i("p", {staticClass: "uploader-meta"}, [t._v("共 " + t._s(t.options.imgList.length) + " 张，还能上传 " + t._s(9 - t.options.imgList.length) + " 张")]), t._v(" "), i("i", {
                staticClass: "bui-icon close-popup icon-close_small",
                on: {
                    click: function (e) {
                        t.clearPopup(!0)
                    }
                }
            }), t._v(" "), i("div", {staticClass: "bui-box upload-box"}, [i("form", {
                ref: "fileElemHolder",
                staticStyle: {display: "none"}
            }, [i("input", {
                ref: "fileElem",
                attrs: {id: "fileElem", type: "file", accept: "image/*", multiple: "multiple"},
                on: {change: t.handleFile}
            })]), t._v(" "), i("ul", {staticClass: "upload-img-list"}, [t._l(t.options.imgList, function (e, n) {
                return i("li", {staticClass: "upload-img-item"}, [i("div", {staticClass: "img-wrap"}, [i("img", {attrs: {src: e}})]), t._v(" "), i("i", {
                    staticClass: "bui-icon remove-img icon-close_small",
                    on: {
                        click: function (i) {
                            t.removeImg(e, n)
                        }
                    }
                })])
            }), t._v(" "), t._l(t.options.loadingList, function (t) {
                return i("li", {staticClass: "upload-img-item upload-img-loading"}, [i("img", {
                    attrs: {
                        src: "http://s3b.pstatp.com/toutiao/resource/toutiao_web/static/style/image/loading_50c5e3e.gif",
                        alt: "上传中"
                    }
                })])
            }), t._v(" "), t.options.imgList.length < 9 ? i("li", {
                staticClass: "upload-img-item upload-img-add",
                attrs: {ga_event: "user_ugc_img_upload"},
                on: {click: t.uploadActionClick}
            }, [i("tt-icon", {attrs: {type: "add_small", size: "30", color: "#ddd"}})], 1) : t._e()], 2)])])])])
        }, staticRenderFns: []
    }
}, function (t, e) {
    t.exports = {
        render: function () {
            var t = this, e = t.$createElement, i = t._self._c || e;
            return i("div", {
                ref: "player",
                staticClass: "player",
                class: {
                    "theatre-btn-visible": t.theatreBtnVisible,
                    "mini-btn-visible": t.miniBtnVisible,
                    float: t.isFloating,
                    transitionable: t.showTransition
                },
                style: {width: t.playerWidth + "px", height: t.playerHeight + "px"},
                attrs: {oncontextmenu: "return false"}
            }, [i("div", {
                directives: [{
                    name: "show",
                    rawName: "v-show",
                    value: t.isBeforeShowing,
                    expression: "isBeforeShowing"
                }], staticClass: "before", on: {click: t.startPlay}
            }, [i("img", {
                directives: [{name: "lazy", rawName: "v-lazy", value: t.posterUrl, expression: "posterUrl"}],
                attrs: {alt: ""}
            }), t._v(" "), i("span", {staticClass: "play-btn"}, [i("tt-icon", {
                attrs: {
                    type: "playvedio",
                    color: "#fff",
                    size: "30"
                }
            })], 1), t._v(" "), t.duration ? i("span", {staticClass: "duration"}, [i("tt-icon", {
                attrs: {
                    type: "playvedio",
                    size: "8"
                }
            }), i("em", [t._v(t._s(t.duration))])], 1) : t._e()]), t._v(" "), i("div", {
                ref: "playerWrap",
                staticClass: "player-wrap"
            }, [i("div", {
                staticClass: "player-inner",
                attrs: {id: t.id}
            }), t._v(" "), i("div", {staticClass: "action-line"}, [i("tt-icon", {
                attrs: {
                    type: "close_small",
                    size: "18"
                }, nativeOn: {
                    click: function (e) {
                        t.closeMini(e)
                    }
                }
            }), t._v(" "), i("span", {on: {mousedown: t.startDrag}}, [t._v("按住该区域可拖动小窗")])], 1)]), t._v(" "), i("div", {
                directives: [{
                    name: "show",
                    rawName: "v-show",
                    value: t.isNextShowing,
                    expression: "isNextShowing"
                }], staticClass: "next"
            }, [i("div", {
                directives: [{
                    name: "show",
                    rawName: "v-show",
                    value: t.isNextOneShowing,
                    expression: "isNextOneShowing"
                }], staticClass: "next-one"
            }, [i("p", {staticClass: "info"}, [t._v("接下来播放")]), t._v(" "), i("h3", {staticClass: "title"}, [t._v(t._s(t.nextOneTitle))]), t._v(" "), i("i", {on: {click: t.cancel}}, [i("img", {
                attrs: {
                    src: t.countdownAddrR,
                    alt: "",
                    width: "78",
                    height: "78"
                }
            })]), t._v(" "), i("div", [i("i", {
                staticClass: "cancel",
                on: {click: t.cancel}
            }, [t._v("取消播放")])])]), t._v(" "), i("div", {
                directives: [{
                    name: "show",
                    rawName: "v-show",
                    value: t.isNextListShowing,
                    expression: "isNextListShowing"
                }], staticClass: "next-list"
            }, [i("ul", t._l(t.siblings, function (e) {
                return i("li", {key: e.link}, [i("a", {
                    attrs: {
                        href: e.link,
                        ga_event: "click_relavant_video"
                    }
                }, [i("img", {
                    directives: [{name: "lazy", rawName: "v-lazy", value: e.img, expression: "item.img"}],
                    attrs: {alt: "相关视频"}
                }), t._v(" "), i("div", {staticClass: "title"}, [i("h4", [t._v(t._s(e.title))])])])])
            })), t._v(" "), i("div", {staticClass: "replay-wrap"}, [i("i", {
                staticClass: "replay",
                on: {click: t.replay}
            }, [t._v("重播")])])])])])
        }, staticRenderFns: []
    }
}, function (t, e) {
    t.exports = {
        render: function () {
            var t = this, e = t.$createElement, i = t._self._c || e;
            return i("div", {staticClass: "wendaBox upload-box"}, [i("input", {
                directives: [{
                    name: "model",
                    rawName: "v-model.trim",
                    value: t.inputTitle,
                    expression: "inputTitle",
                    modifiers: {trim: !0}
                }],
                ref: "title",
                staticClass: "wenda-title-input wenda-input",
                class: {warning: t.titleInvalid},
                attrs: {type: "text", placeholder: "请输入问题标题（4-40字）"},
                domProps: {value: t.inputTitle},
                on: {
                    input: function (e) {
                        e.target.composing || (t.inputTitle = e.target.value.trim())
                    }, blur: function (e) {
                        t.$forceUpdate()
                    }
                }
            }), t._v(" "), i("textarea", {
                directives: [{
                    name: "model",
                    rawName: "v-model.trim",
                    value: t.inputContent,
                    expression: "inputContent",
                    modifiers: {trim: !0}
                }],
                ref: "content",
                staticClass: "wenda-content-input wenda-input",
                class: {warning: t.contentInvalid},
                attrs: {placeholder: "添加问题背景描述（选填，0-40字）"},
                domProps: {value: t.inputContent},
                on: {
                    keyup: function (e) {
                        t.contentKeyup(e.target.value)
                    }, input: function (e) {
                        e.target.composing || (t.inputContent = e.target.value.trim())
                    }, blur: function (e) {
                        t.$forceUpdate()
                    }
                }
            }), t._v(" "), i("p", {staticClass: "words-number"}, [t._v(t._s(t.inputLength) + "/40字")]), t._v(" "), i("div", {staticClass: "bui-box upload-footer"}, [i("div", {staticClass: "bui-left"}, [i("span", {
                staticClass: "show-uploader",
                on: {click: t.togglePopup}
            }, [i("tt-icon", {
                attrs: {
                    type: "pic_tool",
                    size: "15",
                    color: "#ed4040"
                }
            }), t._v(" "), i("span", [t._v("添加图片")])], 1), t._v(" "), i("a", {
                staticClass: "show-uploader",
                attrs: {href: "//www.wukong.com", target: "_blank"}
            }, [i("tt-icon", {
                attrs: {
                    type: "ask_tool",
                    size: "15",
                    color: "#ed4040"
                }
            }), t._v(" "), i("span", [t._v("更多问答")])], 1)]), t._v(" "), i("div", {staticClass: "bui-right"}, [i("span", {staticClass: "msg-tip"}, [t._v(t._s(t.msgTip))]), t._v(" "), i("a", {
                staticClass: "upload-publish",
                class: {active: t.uploadReady},
                on: {click: t.publishImg}
            }, [t._v("发布")])])]), t._v(" "), i("div", {
                directives: [{
                    name: "show",
                    rawName: "v-show",
                    value: t.isShowPopup,
                    expression: "isShowPopup"
                }], staticClass: "uploader-popup"
            }, [i("div", {staticClass: "wendaUploadBox"}, [i("p", {staticClass: "uploader-title"}, [t._v("本地上传")]), t._v(" "), i("p", {staticClass: "uploader-meta"}, [t._v("共 " + t._s(t.options.imgList.length) + " 张，还能上传 " + t._s(3 - t.options.imgList.length) + " 张")]), t._v(" "), i("i", {
                staticClass: "bui-icon close-popup icon-close_small",
                on: {
                    click: function (e) {
                        t.clearPopup(!0)
                    }
                }
            }), t._v(" "), i("div", {staticClass: "bui-box upload-box"}, [i("form", {
                ref: "fileElemHolder",
                staticStyle: {display: "none"}
            }, [i("input", {
                ref: "fileElem",
                attrs: {id: "fileElem", type: "file", accept: "image/*", multiple: "multiple"},
                on: {change: t.handleFile}
            })]), t._v(" "), i("ul", {staticClass: "upload-img-list"}, [t._l(t.options.imgList, function (e, n) {
                return i("li", {staticClass: "upload-img-item"}, [i("div", {staticClass: "img-wrap"}, [i("img", {attrs: {src: e}})]), t._v(" "), i("i", {
                    staticClass: "bui-icon remove-img icon-close_small",
                    on: {
                        click: function (i) {
                            t.removeImg(e, n)
                        }
                    }
                })])
            }), t._v(" "), t._l(t.options.loadingList, function (t) {
                return i("li", {staticClass: "upload-img-item upload-img-loading"}, [i("img", {
                    attrs: {
                        src: "http://s3b.pstatp.com/toutiao/resource/toutiao_web/static/style/image/loading_50c5e3e.gif",
                        alt: "上传中"
                    }
                })])
            }), t._v(" "), t.options.imgList.length < 3 ? i("li", {
                staticClass: "upload-img-item upload-img-add",
                attrs: {ga_event: "user_wenda_upload"},
                on: {click: t.uploadActionClick}
            }, [i("tt-icon", {attrs: {type: "add_small", size: "30", color: "#ddd"}})], 1) : t._e()], 2)])])])])
        }, staticRenderFns: []
    }
}, function (t, e) {
    t.exports = {
        render: function () {
            var t = this, e = t.$createElement, i = t._self._c || e;
            return i("div", {staticClass: "videoBox upload-box"}, [i("textarea", {
                directives: [{
                    name: "model",
                    rawName: "v-model.trim",
                    value: t.inputContent,
                    expression: "inputContent",
                    modifiers: {trim: !0}
                }],
                ref: "title",
                staticClass: "title-input",
                class: {warning: t.inputInvalid},
                attrs: {placeholder: "视频标题（30 字以内）"},
                domProps: {value: t.inputContent},
                on: {
                    keyup: function (e) {
                        t.inputKeyup(e.target.value)
                    }, input: function (e) {
                        e.target.composing || (t.inputContent = e.target.value.trim())
                    }, blur: function (e) {
                        t.$forceUpdate()
                    }
                }
            }), t._v(" "), i("p", {staticClass: "words-number"}, [t._v(t._s(t.inputLength) + "/30字")]), t._v(" "), i("div", {staticClass: "bui-box upload-footer"}, [i("div", {staticClass: "bui-left"}, [i("span", {
                staticClass: "show-video-uploader show-uploader",
                attrs: {ga_event: "user_ugc_video_open"},
                on: {click: t.togglePopup}
            }, [i("tt-icon", {
                attrs: {
                    type: "video_tool",
                    size: "20",
                    color: "#2a90d7"
                }
            }), t._v(" "), i("span", [t._v("添加视频")])], 1)]), t._v(" "), i("div", {staticClass: "bui-right"}, [i("span", {staticClass: "msg-tip"}, [t._v(t._s(t.msgTip))]), t._v(" "), i("a", {
                staticClass: "upload-publish",
                class: {active: t.uploadReady},
                on: {click: t.publishVideo}
            }, [t._v("发布")])])]), t._v(" "), i("div", {
                directives: [{
                    name: "show",
                    rawName: "v-show",
                    value: t.isShowPopup,
                    expression: "isShowPopup"
                }], staticClass: "uploader-popup"
            }, [i("div", {staticClass: "videoUploadBox"}, [i("p", {staticClass: "uploader-title"}, [t._v("上传视频")]), t._v(" "), i("p", {staticClass: "uploader-meta"}, [t._v("发布后，视频将出现在首页和个人页")]), t._v(" "), i("i", {
                staticClass: "bui-icon close-popup icon-close_small",
                on: {
                    click: function (e) {
                        t.clearPopup(!0)
                    }
                }
            }), t._v(" "), i("div", {
                directives: [{
                    name: "show",
                    rawName: "v-show",
                    value: t.options.isInitShow,
                    expression: "options.isInitShow"
                }], staticClass: "bui-box upload-box"
            }, [i("form", {ref: "fileElemHolder", staticStyle: {display: "none"}}, [i("input", {
                ref: "fileElem",
                attrs: {id: "fileElem", type: "file", accept: "video/mp4,video/x-m4v,video/*"},
                on: {change: t.handleFile}
            })]), t._v(" "), i("div", {
                staticClass: "bui-left upload-cover upload-cover-add",
                attrs: {ga_event: "user_ugc_video_upload"},
                on: {click: t.uploadActionClick}
            }), t._v(" "), t._m(0)]), t._v(" "), t.options.isInitShow ? t._e() : i("div", {
                staticClass: "bui-box upload-box"
            }, [i("div", {staticClass: "bui-left upload-cover upload-cover-loading"}, [t.options.poster ? i("img", {attrs: {src: t.options.poster}}) : t._e()]), t._v(" "), i("div", {staticClass: "bui-right upload-info"}, [i("h3", [t._v(t._s(t.options.title))]), t._v(" "), i("div", {staticStyle: {"font-size": "0px"}}, [t.options.isTranscoding ? i("span", {staticClass: "upload-status"}, [t._v("转码中...")]) : t._e(), t._v(" "), t.options.isTranscoding ? t._e() : i("span", {staticClass: "upload-status"}, [t._v(t._s(t.options.statusMsg))]), t._v(" "), t.options.isTranscoding ? t._e() : i("span", {
                staticClass: "upload-ctr",
                on: {click: t.onCtrClick}
            }, [t._v(t._s(t.options.ctrMsg))])]), t._v(" "), i("div", {staticClass: "progress"}, [i("div", {
                staticClass: "progress-rate",
                style: {width: t.options.progress + "%"}
            })])])])])])])
        }, staticRenderFns: [function () {
            var t = this, e = t.$createElement, i = t._self._c || e;
            return i("div", {staticClass: "bui-right upload-warning"}, [i("span", [t._v("支持绝大多数的视频格式，大小不超过250M，请勿上传反动色情等违法视频。")])])
        }]
    }
}, , function (t, e) {
    t.exports = {
        render: function () {
            var t = this, e = t.$createElement, i = t._self._c || e;
            return i("div", {staticClass: "login inner"}, [i("p", {staticClass: "login-msg"}, [t._v("\n    登录后可以保存您的浏览喜好、评论、收藏，并与APP同步更可以发布微头条\n  ")]), t._v(" "), t._m(0), t._v(" "), i("ul", {
                staticClass: "third-login",
                on: {click: t.snsLogin}
            }, [t._m(1), t._v(" "), t._m(2)])])
        }, staticRenderFns: [function () {
            var t = this, e = t.$createElement, i = t._self._c || e;
            return i("a", {attrs: {href: "https://sso.toutiao.com"}}, [i("button", {staticClass: "login-button"}, [t._v("登录")])])
        }, function () {
            var t = this, e = t.$createElement, i = t._self._c || e;
            return i("li", {staticClass: "sns qq", attrs: {"data-pid": "qzone_sns"}}, [i("span", [t._v("QQ")])])
        }, function () {
            var t = this, e = t.$createElement, i = t._self._c || e;
            return i("li", {staticClass: "sns weixin", attrs: {"data-pid": "weixin"}}, [i("span", [t._v("微信")])])
        }]
    }
}, , function (t, e) {
    t.exports = {
        render: function () {
            var t = this, e = t.$createElement, i = t._self._c || e;
            return i("div", {staticClass: "outer"}, [t.loginInfo.id ? i("Logged", {attrs: {loginInfo: t.loginInfo}}) : i("Login")], 1)
        }, staticRenderFns: []
    }
}, , function (t, e) {
    t.exports = {
        render: function () {
            var t = this, e = t.$createElement, i = t._self._c || e;
            return i("div", [i("tt-header", {
                attrs: {
                    options: t.headerInfo,
                    showUser: !1
                }
            }), t._v(" "), i("div", {staticClass: "bui-box container"}, [i("div", {
                staticClass: "bui-left index-channel",
                attrs: {ga_event: "channel_click"}
            }, [i("channel", {
                attrs: {
                    tag: t.tag,
                    local: t.province
                }
            })], 1), t._v(" "), i("div", {staticClass: "bui-left index-content"}, [i("news-slide"), t._v(" "), i("feed-box", {
                attrs: {
                    category: t.category,
                    url: t.url,
                    qhAdSupport: t.qhAdSupport,
                    containerCheckCount: 3
                }
            })], 1), t._v(" "), i("div", {
                staticClass: "bui-right index-right-bar",
                attrs: {id: "rightModule"}
            }, [i("div", {
                staticClass: "search-wrapper",
                attrs: {ga_event: "index_search_click"}
            }, [i("search-box")], 1), t._v(" "), i("div", [i("is-login", {attrs: {loginInfo: t.loginInfo}})], 1), t._v(" "), i("div", {
                staticClass: "right-top-1 right-iframe-img right-img",
                attrs: {name: "home_right*top_1_zy"}
            }), t._v(" "), i("div", {staticClass: "news-struct bui-box"}, [i("div", {
                staticClass: "bui-box",
                class: {"module-fixed": t.newsHover},
                attrs: {id: "hotNewsWrap"}
            }, [i("hot-news", {attrs: {count: 4, title: "24小时热闻"}}), t._v(" "), i("div", {
                directives: [{
                    name: "show",
                    rawName: "v-show",
                    value: t.isShowNewsImg,
                    expression: "isShowNewsImg"
                }],
                staticClass: "imagindexhover right-img",
                attrs: {id: "imagindexhover", name: "home_right*bottom_1_zy"}
            })], 1)]), t._v(" "), i("div", {
                staticClass: "right-iframe-img right-img",
                attrs: {name: "home_right*top_2_zy"}
            }), t._v(" "), i("hot-videos", {attrs: {count: 7}}), t._v(" "), i("div", {
                staticClass: "right-iframe-img right-img",
                attrs: {name: "home_right*top_3_zy"}
            }), t._v(" "), i("hot-images", {attrs: {count: 8}}), t._v(" "), i("more-links", {attrs: {title: "更多"}}), t._v(" "), i("friend-links", {attrs: {title: "友情链接"}}), t._v(" "), i("company")], 1)]), t._v(" "), i("toolbar", {
                attrs: {
                    "show-refresh": !0,
                    "show-report": !0,
                    "refresh-method": t.refreshFeed
                }
            })], 1)
        }, staticRenderFns: []
    }
}, function (t, e) {
    t.exports = {
        render: function () {
            var t = this, e = t.$createElement, i = t._self._c || e;
            return t.item.video_id ? i("div", {staticClass: "bui-box video-mode"}, [i("div", {staticClass: "bui-left video-mode-lbox"}, [i("player", {
                attrs: {
                    "video-id": t.item.video_id,
                    width: 325,
                    height: 183,
                    "poster-url": t.posterUrl,
                    "theatre-btn-visible": !1,
                    "mini-btn-visible": !0,
                    "destroy-count": t.destroyCount,
                    duration: t.item.video_duration_str,
                    mute: t.mute
                },
                on: {
                    "zoom-change": t.zoomChange,
                    "close-mini": t.zoomRecover,
                    "video-end": t.videoEndHandler,
                    "volume-change": t.volumeChangeHandler,
                    "full-size": t.fullSizeHandler,
                    "video-play": t.playHandler
                }
            })], 1), t._v(" "), i("div", {staticClass: "video-mode-rbox"}, [i("div", {
                staticClass: "title-box",
                attrs: {ga_event: "video_title_click"}
            }, [i("a", {
                staticClass: "link",
                attrs: {href: t.item.source_url, target: "_blank"},
                on: {click: t.openDetail}
            }, [t._v(t._s(t.item.title))])]), t._v(" "), i("div", {staticClass: "bui-box footer-bar"}, [t.item.media_url ? [i("a", {
                staticClass: "footer-bar-action media-avatar",
                attrs: {href: t.item.media_url, target: "_blank", ga_event: "video_avatar_click"}
            }, [i("img", {
                directives: [{
                    name: "lazy",
                    rawName: "v-lazy",
                    value: t.item.media_avatar_url,
                    expression: "item.media_avatar_url"
                }]
            })]), t._v(" "), i("a", {
                staticClass: "footer-bar-action source",
                attrs: {href: t.item.media_url, target: "_blank", ga_event: "video_name_click"}
            }, [t._v(t._s(t.item.source))]), t._v(" "), i("span", {staticClass: "footer-bar-action"}, [t._v("⋅")]), t._v(" "), i("a", {
                staticClass: "footer-bar-action source",
                attrs: {href: t.item.source_url, target: "_blank", ga_event: "video_frequency_click"},
                on: {click: t.openDetail}
            }, [t._v(t._s(t._f("formatCount")(t.item.video_play_count)) + "次播放")])] : [i("a", {
                staticClass: "footer-bar-action media-avatar",
                class: t.item.avatar_style,
                attrs: {href: "/search/?keyword=" + t.item.source, ga_event: "video_avatar_click"}
            }, [t._v(t._s(t.item.source_tag))]), t._v(" "), i("a", {
                staticClass: "footer-bar-action source",
                attrs: {href: "/search/?keyword=" + t.item.source, target: "_blank", ga_event: "video_name_click"}
            }, [t._v(t._s(t.item.source))])]], 2), t._v(" "), t.labels.length ? i("div", {staticClass: "labels"}, [i("ul", t._l(t.labels, function (e) {
                return i("li", [i("a", {
                    attrs: {
                        href: "http://www.toutiao.com/search/?keyword=" + e,
                        target: "_blank",
                        ga_event: "video_label_click"
                    }
                }, [t._v(t._s(e))])])
            }))]) : t._e(), t._v(" "), i("dislike", {attrs: {group_id: t.item.group_id}})], 1)]) : t._e()
        }, staticRenderFns: []
    }
}, function (t, e) {
    t.exports = {
        render: function () {
            var t = this, e = t.$createElement, i = t._self._c || e;
            return i("div", {staticClass: "logged inner"}, [i("div", {staticClass: "top"}, [t._m(0), t._v(" "), i("div", [i("a", {
                attrs: {
                    href: "/c/user/" + t.loginInfo.id + "/",
                    target: "_blank"
                }
            }, [i("img", {
                staticClass: "head",
                attrs: {src: t.loginInfo.avatarUrl}
            })]), t._v(" "), i("p", {staticClass: "name"}, [i("a", {
                attrs: {
                    href: "/c/user/" + t.loginInfo.id + "/",
                    target: "_blank"
                }
            }, [i("span", [t._v(t._s(t.loginInfo.userName))])])])])]), t._v(" "), i("ul", {staticClass: "bottom"}, [i("li", [i("a", {
                attrs: {
                    href: "/c/user/" + t.loginInfo.id + "/?tab=following",
                    target: "_blank"
                }
            }, [i("p", {staticClass: "num"}, [t._v(t._s(t.following))]), i("br"), t._v(" "), i("p", {staticClass: "word"}, [t._v("关注")])])]), t._v(" "), i("li", {staticClass: "line"}), t._v(" "), i("li", [i("a", {
                attrs: {
                    href: "/c/user/relation/" + t.loginInfo.id + "/?tab=followed",
                    target: "_blank"
                }
            }, [i("p", {staticClass: "num"}, [t._v(t._s(t.fans))]), i("br"), t._v(" "), i("p", {staticClass: "word"}, [t._v("粉丝")])])])])])
        }, staticRenderFns: [function () {
            var t = this, e = t.$createElement, i = t._self._c || e;
            return i("p", {staticClass: "logout"}, [i("a", {attrs: {href: "https://sso.toutiao.com/logout/"}}, [i("span", [t._v("退出登录")])])])
        }]
    }
}, , , function (t, e) {
}]);
