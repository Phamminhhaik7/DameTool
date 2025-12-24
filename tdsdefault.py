
import json
from datetime import datetime
import random
import xml.etree.ElementTree as ET
import re
import os,sys
import subprocess
import time
import logging
import math
try:
    import requests
except:
    os.system("pip install requests")
    import requests
try:
    from bs4 import BeautifulSoup
except:
    os.system("pip install beautifulsoup4")
    from bs4 import BeautifulSoup
# --- Cấu hình API và Global Session ---
USE_ADB = False # Biến toàn cục để kiểm soát Auto Click
# --- Cấu hình Log ---
if os.path.exists("tds_error.log"):
    os.remove("tds_error.log")
logging.basicConfig(
    filename='tds_error.log', 
    level=logging.INFO,
    format='%(asctime)s - %(levelname)s - [Line %(lineno)d] - %(message)s' 
)

# --- Class Tương tác API ---
class TraodoisubApi:
    def __init__(self, token):
        self.session = requests.Session()
        self.tds_token = token

    def _make_request(self, method, endpoint):
        # Thêm access_token vào URL cho các request TraoDoiSub
        url = f"https://traodoisub.com/api/{endpoint}"
        
        try:
            response = self.session.request(method, url)
            try:
                res_json = response.json()
                if res_json.get("error") == "Thao tác quá nhanh vui lòng chậm lại":
                    return res_json
                if res_json.get("error"):
                    logging.error(f"Data: {res_json} | URL: {url}")
                elif res_json.get("success") and res_json.get("cache"):
                    logging.error(f"Data: {res_json} | URL: {url}")
                elif not res_json.get("data"):
                    logging.error(f"Data: {res_json} | URL: {url}")
                return res_json
            except json.JSONDecodeError:
                logging.error(f"Error: Invalid JSON response. Raw content: {response.text}")
                if response.status_code != 200:
                    logging.error(f"HTTP Error {response.status_code}, Data: {response.text} | URL: {url}")
                    return None
                
        except requests.exceptions.RequestException as e:
            logging.error(f"Request Exception: {e}")
            return None
        except Exception as e:
            logging.error(f"Unexpected Error: {e}")
            return None

    ## --- CÁC HÀM API CHÍNH ---

    def get_account_info(self):
        """Lấy thông tin tài khoản (coins, user, xudie)."""
        return self._make_request('GET', endpoint="?fields=profile&access_token="+self.tds_token)
    def set_config_id(self, service, config_id):
        """Set config_id cho tài khoản."""
        return self._make_request('GET', endpoint="?fields="+service+"&id="+config_id+"&access_token="+self.tds_token)
    def get_job(self, type):
        """Lấy thông tin nhiệm vụ."""
        return self._make_request('GET', endpoint="?fields="+type+"&access_token="+self.tds_token)
    def cache_job(self, type, id_job):
        """Lấy thông tin nhiệm vụ."""
        return self._make_request('GET', endpoint="coin/?type="+type+"&id="+id_job+"&access_token="+self.tds_token)
    def claim_job(self, type, id_job):
        """Lấy thông tin nhiệm vụ."""
        return self._make_request('GET', endpoint="coin/?type="+type+"&id="+id_job+"&access_token="+self.tds_token)
# --- HÀM HỖ TRỢ VÀ LOGIC TỰ ĐỘNG ---

def bloger_cawl(url):
    try:
        html = requests.get(url).text
        soup = BeautifulSoup(html, "html.parser")
        desc = soup.find("meta", {"property": "og:description"})["content"]
        return desc
    except Exception as e:
        return None

def check_and_create_key():
    now = datetime.now()
    """Kiểm tra và tạo key nếu chưa tồn tại."""
    while True:
        if os.path.exists("tds_key.json"):
            try:
                with open("tds_key.json", "r") as f:
                    data = json.load(f)
                    key_data = data.get("key")
                # Kiểm tra: key tồn tại VÀ chuỗi ngày (VD: "21") có nằm trong key không
                if key_data and str(now.day) in key_data:
                    return True 
                else:
                    os.remove("tds_key.json")
                    continue
            except:
                os.remove("tds_key.json")
                continue
        else:
            # 1. Tạo key ngẫu nhiên theo ngày (Ví dụ: HaiCoding_Key_21_ABCD)
            random_str = ''.join(random.choices("ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789", k=4))
            correct_key = f"HaiCoding_Key{now.day}_{random_str}"
            
            # 2. Tạo link lấy key qua mualink.vip
            url = requests.get(f"https://mualink.vip/api?api=a1dd0576742bb72beb88d87748883cf75ab77494&url=https://dameconghe.github.io/getkey/?key={correct_key}").json()
            if url.get("status") != "success":
                time.sleep(2)
                continue
            mualink_url = url.get("shortenedUrl")
            # 3. Giao diện yêu cầu nhập mã
            print_color("●▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬●", 0, 255, 255)
            print_color("  🔑 HỆ THỐNG KEY NGÀY MIỄN PHÍ", 255, 255, 0)
            print_color(f"  📅 Hôm nay: {now.strftime('%d/%m/%Y')}", 200, 200, 200)
            print_color("●▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬●", 0, 255, 255)
            print_color("👉 Vui lòng vượt link sau để lấy Key:", 0, 255, 255)
            print_color(f"🔗 {mualink_url}", 255, 255, 255)
            print_color("Mở link sau 3s", 255, 255, 255)
            time.sleep(3)
            open_link(mualink_url)

            while True:
                input_key = input(color_text("\n🔑 Nhập Key đã lấy được: ", 0, 255, 255)).strip()
                if input_key == correct_key:
                    with open("tds_key.json", "w") as f:
                        json.dump({"key": input_key}, f)
                    print_color("\n✅ Xác thực thành công! Đang vào Tool...", 0, 255, 0)
                    time.sleep(2)
                    sys.stdout.write("\033[F\033[K\033[F\033[K\033[F\033[K\033[F\033[K\033[F\033[K\033[F\033[K\033[F\033[K\033[F\033[K\033[F\033[K\033[F\033[K\033[F\033[K")
                    sys.stdout.flush()
                    return True
                else:
                    print_color("❌ Key không đúng! Vui lòng kiểm tra lại.", 255, 0, 0)

def load_tds_token():
    """Tải TDS_token từ tệp hoặc yêu cầu người dùng nhập."""
    data_file_path = "tds_token.json"
    
    # Nếu file đã tồn tại, đọc token từ file
    if os.path.exists(data_file_path):
        with open(data_file_path, "r") as f:
            user_data = json.load(f)
        if user_data.get("tds_token"):
            return user_data

    # Nếu chưa có token, thực hiện đăng nhập
    print_color("--- 🔐 ĐĂNG NHẬP TRAODOISUB ---", 0, 255, 255)
    print("  [1] Sử dụng TDS Token (Nhanh)")
    print("  [2] Sử dụng Username & Password")
    
    user_data = {}
    tds_token = None
    while True:
        choice = input("👉 Lựa chọn của bạn: ").strip()

        if choice == "1":
            tds_token = input(color_text("🔑 Nhập TDS_token: ", 0, 255, 255)).strip()
            user_data = {"tds_token": tds_token}
            sys.stdout.write("\033[F\033[K\033[F\033[K\033[F\033[K\033[F\033[K")
            sys.stdout.flush()
            break
        elif choice == "2":
            username = input("👤 Nhập Username: ").strip()
            password = input("🔑 Nhập Password: ").strip()
            
            # Thực hiện login để lấy PHPSESSID
            login_data = {
                'username': username,
                'password': password,
            }
            
            try:
                session = requests.Session()
                response = session.post('https://traodoisub.com/scr/login.php', data=login_data, timeout=10)
                
                if "success" in response.text.lower() or response.status_code == 200:
                    # Lấy token từ trang setting
                    res_setting = session.get('https://traodoisub.com/view/setting/load.php', timeout=10)
                    tds_token = res_setting.json().get("tokentds")
                    
                    if tds_token:
                        print_color(f"✅ Đăng nhập thành công! Token: {tds_token}", 0, 255, 0)
                        user_data = {
                            "username": username,
                            "password": password,
                            "tds_token": tds_token
                        }
                        sys.stdout.write("\033[F\033[K\033[F\033[K\033[F\033[K\033[F\033[K\033[F\033[K\033[F\033[K\033[F\033[K")
                        sys.stdout.flush()
                    else:
                        print_color("❌ Lỗi: Không thể lấy được Token TDS từ tài khoản này.", 255, 0, 0)
                else:
                    print_color(f"❌ Đăng nhập thất bại: {response.text}", 255, 0, 0)
            except Exception as e:
                print_color(f"❌ Lỗi kết nối khi đăng nhập: {e}", 255, 0, 0)
            break
    # Lưu thông tin nếu có token
    if tds_token:
        with open(data_file_path, "w") as f:
            json.dump(user_data, f, indent=2, ensure_ascii=False)
        return user_data
    else:
        print_color("❌ Lỗi: Không có token để tiếp tục.", 255, 0, 0)
        sys.exit(1)

def get_accounts_from_web(username, password, platform_endpoint):
    """
    Hàm dùng chung để quét danh sách tài khoản từ web TraoDoiSub.
    platform_endpoint: 'chtiktok', 'chfacebook', hoặc 'chinstagram'
    """
    try:
        login_payload = {'username': username, 'password': password}
        scrape_session = requests.Session()
        scrape_session.post('https://traodoisub.com/scr/login.php', data=login_payload)
        
        res_page = scrape_session.get(f"https://traodoisub.com/view/{platform_endpoint}")
        soup = BeautifulSoup(res_page.text, "html.parser")
        table_data = soup.find(id="table-purchase-body") or soup.find("table")
        
        unique_ids = []
        if table_data:
            for row in table_data.find_all("tr"):
                cells = row.find_all(["th", "td"])
                if len(cells) >= 2:
                    tid = cells[0].get_text(strip=True)
                    uid = cells[1].get_text(strip=True)
                    if tid.isdigit() or (tid and "ID" not in tid):
                        unique_ids.append(uid)
        return unique_ids
    except Exception as e:
        print_color(f"❌ Lỗi khi quét tài khoản {platform_endpoint}: {e}", 255, 0, 0)
        return []

def open_link(url: str):
    """
    Hàm mở link tự động.
    Sử dụng lệnh phù hợp với nhiều hệ điều hành (mô phỏng).
    """
    if os.name == 'nt': # Windows
        os.system(f"start {url}")
    elif os.uname().sysname == 'Darwin': # macOS
        os.system(f"open {url}")
    elif os.environ.get('TERMUX_VERSION'): # Termux
        os.system(f"termux-open {url}")
    else: # Linux
        os.system(f"xdg-open {url}")
    # print(f"🚀 Đã mở link: {url}") # Để tránh spam console, chỉ log khi cần

def get_gradient_color(step):
    """Tạo màu RGB theo vòng lặp sin để tạo hiệu ứng cầu vồng"""
    r = int(math.sin(0.3 * step + 0) * 127 + 128)
    g = int(math.sin(0.3 * step + 2) * 127 + 128)
    b = int(math.sin(0.3 * step + 4) * 127 + 128)
    return f"\033[38;2;{r};{g};{b}m"

def print_gradient_text(text):
    """In văn bản với hiệu ứng gradient từng ký tự"""
    for i, char in enumerate(text):
        color = get_gradient_color(i)
        sys.stdout.write(f"{color}{char}")
    sys.stdout.write("\033[0m\n") # Reset màu và xuống dòng
    sys.stdout.flush()

def color_text(text, r, g, b):
    """Trả về chuỗi văn bản có mã màu RGB (không in)"""
    return f"\033[38;2;{r};{g};{b}m{text}\033[0m"

def print_color(text, r, g, b):
    """In văn bản với một màu RGB cố định"""
    sys.stdout.write(color_text(text, r, g, b) + "\n")
    sys.stdout.flush()

def print_banner():
    """In Logo DEVDRIP lớn với hiệu ứng gradient"""
    banner = """
    __  __      _                 
   / / / /___ _(_)                
  / /_/ / __ `/ /                 
 / __  / /_/ / /                  
/_/_/_/\__,_/_/    ___            
  / ____/ ___  ____/ (_)___  ____ _
 / /    / __ \/ __  / / __ \/ __ `/
/ /___ / /_/ / /_/ / / / / / /_/ / 
\____/ \____/\__,_/_/_/ /_/\__, /  
                           ____/   
 -- Advanced Agentic Coding --
    """
    lines = banner.split('\n')
    for i, line in enumerate(lines):
        # Tạo hiệu ứng chuyển màu theo từng dòng (Vertical Gradient)
        print_gradient_text(line)
    print()

def timer_cooldown(count, seconds, uniqueID):
    try:
        last_lines = 1
        for i in range(seconds * 20 + 1):
            # Lấy chiều rộng terminal mỗi lần lặp để xử lý khi xoay màn hình
            try:
                term_width = os.get_terminal_size().columns
            except:
                term_width = 80

            color = get_gradient_color(i)
            reset = "\033[0m"
            remaining = round(seconds - (i / 20), 1)
            if remaining < 0: remaining = 0
            
            # Văn bản hiển thị
            text_plain = f" [#{count}] 🎯 {uniqueID} ➜ ⏳ Đang chờ: {remaining} giây..."
            
            # Ước tính độ dài hiển thị (Emojis chiếm 2 cột trên nhiều terminal)
            # 🎯, ⏳, ➜
            display_len = len(text_plain) + text_plain.count('🎯') + text_plain.count('⏳') + text_plain.count('➜')
            
            # Tính số dòng hiện tại đang chiếm
            current_lines = (display_len // term_width) + 1
            
            # Quay lại đầu vị trí của tin nhắn trước
            if last_lines > 1:
                sys.stdout.write(f"\033[{last_lines - 1}A") # Di chuyển lên N-1 dòng
            sys.stdout.write("\r")
            
            # Xóa sạch từ vị trí con trỏ đến hết màn hình (để xóa rác khi co giãn dòng)
            sys.stdout.write("\033[J")
            
            sys.stdout.write(f"{color}{text_plain}{reset}")
            sys.stdout.flush()
            
            last_lines = current_lines
            if i < seconds * 20:
                time.sleep(0.05)
        print() 
    except KeyboardInterrupt:
        print("\n👋 Đã dừng.")
        exit(1)

def setup_adb_connection(user, xu, xudie):
    """Hỏi người dùng và thiết lập kết nối ADB theo chuẩn Wireless Debugging"""
    global USE_ADB
    print_color("  📱 CẤU HÌNH AUTO CLICK (ADB)", 255, 255, 0)
    print_color("  [1] Sử dụng Auto Click (tích hợp)", 200, 200, 200)
    print_color("  [2] Sử dụng Auto Click ngoài", 200, 200, 200)
    
    while True:
        choice = input(color_text("👉 Lựa chọn của bạn: ", 255, 255, 255)).strip()
        if choice == "1":
            # Kiểm tra nhanh xem đã có thiết bị nào chưa
            devices = adb_shell("adb devices")
            lines = [l for l in devices.strip().split('\n') if l.strip()]
            
            if len(lines) > 1: 
                device_id = lines[1].replace('\tdevice', '')
                print_color(f"✅ Đã tìm thấy thiết bị: {device_id}", 0, 255, 0)
                USE_ADB = True
                time.sleep(1.5)
                break
            
            # Nếu chưa có thiết bị, hỗ trợ Wireless Pairing
            print_color("\n--- 📶 KẾT NỐI WIRELESS ADB ---", 0, 255, 255)
            print("  [1] Ghép nối thiết bị mới (Pair device)")
            print("  [2] Kết nối nhanh (Đã ghép nối trước đó)")
            sub_choice = input(color_text("👉 Chọn: ", 255, 255, 255)).strip()

            if sub_choice == "1":
                print_color("\n📝 HƯỚNG DẪN PAIR:", 255, 255, 0)
                print("1. Vào Wireless Debugging > Pair device with pairing code")
                ip_p = input(color_text("👉 Nhập IP:PORT Ghép nối (vd: 192.168.1.5:37123): ", 0, 255, 255)).strip()
                if ip_p:
                    print_color(f"🔗 Đang thực hiện: adb pair {ip_p}", 0, 255, 255)
                    print_color("⚠️ Vui lòng nhập Pairing Code khi được nhắc!", 255, 255, 0)
                    os.system(f"adb pair {ip_p}")
                
                print_color("\n🔗 BƯỚC TIẾP THEO: KẾT NỐI", 255, 255, 0)
                ip_c = input(color_text("👉 Nhập IP:PORT Kết nối (vd: 192.168.1.5:5555): ", 0, 255, 255)).strip()
                if ip_c:
                    adb_shell(f"adb connect {ip_c}")
            else:
                ip_c = input(color_text("👉 Nhập IP:PORT Kết nối: ", 0, 255, 255)).strip()
                if ip_c:
                    print_color(f"🔗 Đang kết nối tới {ip_c}...", 0, 255, 255)
                    adb_shell(f"adb connect {ip_c}")

            # Kiểm tra lại lần cuối
            check = adb_shell("adb devices")
            if len([l for l in check.strip().split('\n') if "device" in l]) > 1:
                print_color("✅ Kết nối ADB thành công!", 0, 255, 0)
                USE_ADB = True
                time.sleep(1.5)
                break
            else:
                print_color("❌ Kết nối thất bại. Hãy kiểm tra lại IP/Port!", 255, 0, 0)
                continue

        elif choice == "2":
            USE_ADB = False
            print_color("💡 Chế độ: Tự click bằng tay.", 200, 200, 200)
            time.sleep(1)
            break
        else:
            print_color("❌ Vui lòng chọn 1 hoặc 2!", 255, 0, 0)

    os.system('cls' if os.name == 'nt' else 'clear')
    print_banner()
    print_color("●▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬●", 0, 255, 255)
    print_color("  ✅ THÔNG TIN TÀI KHOẢN:", 0, 255, 255)
    print_color(f"  👤 User : {user}", 200, 200, 200)
    print_color(f"  💰 Xu   : {xu}", 255, 215, 0)
    print_color(f"  🔴 Xudie: {xudie}", 255, 99, 71)
    print_color("●▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬●", 0, 255, 255)

def adb_shell(command):
    process = subprocess.Popen(command, shell=True, stdout=subprocess.PIPE)
    output, _ = process.communicate()
    return output.decode('utf-8')

def parse_bounds(bounds_str):
    try:
        coords = re.findall(r'\d+', bounds_str)
        x1, y1, x2, y2 = map(int, coords)
        return (x1 + x2) // 2, (y1 + y2) // 2
    except:
        return None

def click_specific_node(target_text, target_index):
    """Tìm và click vào nút khớp Text và Index trong XML"""
    adb_shell("adb shell uiautomator dump /sdcard/window_dump.xml")
    adb_shell("adb pull /sdcard/window_dump.xml .")
    try:
        tree = ET.parse('window_dump.xml')
        root = tree.getroot()
        for node in root.iter('node'):
            text = node.attrib.get('text', '')
            idx = node.attrib.get('index', '')
            if target_text in text and idx == str(target_index):
                center = parse_bounds(node.attrib.get('bounds'))
                if center:
                    adb_shell(f"adb shell input tap {center[0]} {center[1]}")
                    sys.stdout.write("\033[F\033[K")
                    sys.stdout.flush()
                    return True
        return False
    except: return False

def double_click_center():
    """Lấy kích thước màn hình và double click vào giữa để thả tim"""
    size_str = adb_shell("adb shell wm size")
    match = re.search(r'(\d+)x(\d+)', size_str)
    if match:
        w, h = map(int, match.groups())
        cx, cy = w // 2, h // 2
        adb_shell(f"adb shell input tap {cx} {cy}")
        time.sleep(0.1) # Khoảng nghỉ ngắn giữa 2 lần tap
        adb_shell(f"adb shell input tap {cx} {cy}")
        return True
    return False

def click_to_comlete_job(type_job):
    """Điều hướng xử lý theo loại nhiệm vụ"""
    if type_job == "tiktok_follow":
        # Ưu tiên tìm text 'Follow' (Tiếng Anh)
        return click_specific_node("Follow", "0")
    elif type_job == "instagram_follow":
        # Ưu tiên tìm text 'Theo dõi' (Tiếng Việt)
        return click_specific_node("Theo dõi", "0")
    elif type_job == "instagram_like" or type_job == "tiktok_like":
        # Double click giữa màn hình
        return double_click_center()
    return False

def run_auto_loop(api: TraodoisubApi, service_info, cooldown_time, count_cache_to_claim):
    # Sử dụng .get() để truy cập an toàn, tránh KeyError nếu cấu trúc SERVICE_MAP bị lỗi
    service_type = service_info.get("service")
    cache_type = service_info.get("cache_type")
    claim_type = service_info.get("claim_type")
    is_fixed_claim_id = service_info.get("is_fixed_claim_id")
    
    count_to_infinite = 1
    if not service_type:
        print("❌ Lỗi: Không tìm thấy service_type.")
        return 1
    
    if not claim_type:
        print("❌ Lỗi: Không tìm thấy claim_type.")
        return 1
    
    # ... (giữ nguyên logic kiểm tra khác) ...
    
    print() # Tạo khoảng trống
    print_gradient_text(f"--- BẮT ĐẦU CHẠY AUTO: {service_type.upper()} ---")
    
    # Thử nhận xu dư trước khi bắt đầu
    if is_fixed_claim_id:
        target_claim_id = f"{claim_type}_API"
        claim_response = api.claim_job(claim_type, target_claim_id)
        time.sleep(5)
    while True:
        try:
            job_response = api.get_job(service_type)
            if job_response is None:
                print_color("Thao tác quá nhanh vui lòng chậm lại. Nghỉ 30s", 255, 0, 0)
                time.sleep(30)
                sys.stdout.write("\033[F\033[K")
                sys.stdout.flush()
                continue
            elif job_response and job_response.get("error"):
                msg = job_response.get("error") if job_response else "Không rõ"
                print(f"🔔 {msg}. Đang chờ {job_response.get("countdown")+5}s...", end="")
                time.sleep(job_response.get("countdown")+5)
                sys.stdout.write("\033[F\033[K")
                sys.stdout.flush()
                continue
            elif not job_response.get("data"):
                time.sleep(3) # Nghỉ ngắn trước khi đổi
                return "OUT_OF_JOBS" # Thoát hàm và trả về tín hiệu
            jobs_list = job_response.get("data", [])
            for job in jobs_list:
                uniqueID = job.get("uniqueID")
                link = job.get("link")
                job_id = job.get("id")
                
                # 1. Mở link
                open_link(link)
                
                # 2. Chờ và hiển thị trạng thái
                timer_cooldown(count_to_infinite, cooldown_time, uniqueID)
                
                # Thực hiện click nếu người dùng cho phép
                if USE_ADB:
                    click_to_comlete_job(service_type)

                # 3 & 4. Gửi duyệt and Kiểm tra nhận xu
                if is_fixed_claim_id:
                    time.sleep(2)
                    cache_res = api.cache_job(cache_type, job_id)
                    current_cache = cache_res.get("cache", 0) if cache_res else 0
                    
                    if current_cache >= count_cache_to_claim:
                        print_color(f"🚩 Đang gom {current_cache} nhiệm vụ để nhận xu... ", 0, 255, 255)
                        time.sleep(2)
                        sys.stdout.write("\033[F\033[K")
                        sys.stdout.flush()
                        # Xác định ID claim: Nếu là các loại API cố định (FOLLOW/LIKE) thì dùng chuỗi _API
                        target_claim_id = f"{claim_type}_API" if is_fixed_claim_id else job_id
                        while True:
                            claim_response = api.claim_job(claim_type, target_claim_id)
                            if claim_response and claim_response.get("success") == 200:
                                res_data = claim_response.get("data", {})
                                xu_them = res_data.get("xu_them", "0")
                                job_ok = res_data.get("job_success", "0")
                                tong_xu = res_data.get("xu", "0")
                                print_color("●▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬●", 0, 255, 255)
                                print_color(f"  🎉 THÀNH CÔNG: +{xu_them} XU", 0, 255, 0)
                                print_color(f"  ✅ Hoàn thành: {job_ok} nhiệm vụ", 200, 200, 200)
                                print_color(f"  💰 Tổng số dư: {tong_xu} xu", 255, 215, 0)
                                print_color("●▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬●", 0, 255, 255)
                                break
                            elif claim_response and claim_response.get("countdown"):
                                print_color(f"⏰ Thao tác quá nhanh, nhận xu sau {claim_response.get('countdown') + 5}s...", 255, 255, 0)
                                time.sleep(claim_response.get("countdown") + 5)
                                sys.stdout.write("\033[F\033[K")
                                sys.stdout.flush()
                                continue
                            else:
                                print_color("❌ Lỗi: Nhận xu không thành công!", 255, 0, 0)
                                sys.stdout.write("\033[F\033[K")
                                sys.stdout.flush()
                                break
                else:
                    # Đối với dịch vụ nhận ngay (như COMMENT)
                    claim_response = api.claim_job(claim_type, job_id)
                    while True:
                        if claim_response is None:
                            print_color("❌ Lỗi: Nhận xu không thành công!", 255, 0, 0)
                            sys.stdout.write("\033[F\033[K")
                            sys.stdout.flush()
                            break
                        elif claim_response and claim_response.get("countdown"):
                            print_color(f"⏰ Thao tác quá nhanh, nhận xu sau {claim_response.get('countdown') + 5}s...", 255, 255, 0)
                            time.sleep(claim_response.get("countdown") + 5)
                            sys.stdout.write("\033[F\033[K")
                            sys.stdout.flush()
                            continue
                        elif claim_response and claim_response.get("success") == 200:
                            print_color("●▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬●", 0, 255, 255)
                            print_color(f"  🎉 THÀNH CÔNG: {claim_response.get('data', {}).get('msg', '0')}", 0, 255, 0)
                            print_color(f"  💰 Tổng số dư: {claim_response.get('data', {}).get('xu', '0')} xu", 255, 215, 0)
                            print_color("●▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬●", 0, 255, 255)
                            break
                
                count_to_infinite += 1
                
        except KeyboardInterrupt:
            print("\n👋 Đã dừng chương trình!")
            exit(1)
        except Exception as e:
            print(f"\n❌ Lỗi vòng lặp: {e}")
            time.sleep(5)
    return 0
        

if __name__ == "__main__":
    try:
        # Bắt buộc kiểm tra Key trước khi vào tool
        
        os.system('cls' if os.name == 'nt' else 'clear') # Xóa màn hình cho đẹp
        print_banner()
        check_and_create_key()
        while True:
            if os.path.exists("tds_token.json"):
                print_color("Bạn có muốn đăng nhập tài khoản khác không?", 0, 255, 255)
                print("[1] Đăng nhập tài khoản khác")
                print("[2] Tiếp tục")
                match int(input("👉 Vui lòng chọn: ")):
                    case 1:
                        os.remove("tds_token.json")
                        sys.stdout.write("\033[F\033[K\033[F\033[K\033[F\033[K\033[F\033[K")
                        sys.stdout.flush()
                        continue
                    case 2:
                        sys.stdout.write("\033[F\033[K\033[F\033[K\033[F\033[K\033[F\033[K")
                        sys.stdout.flush()
                        pass
                    case _:
                        print_color("❌ Lỗi: Vui lòng chọn 1 hoặc 2!", 255, 0, 0)
                        continue
            user_data = load_tds_token()
            tds_token = user_data.get("tds_token")
            api = TraodoisubApi(token=tds_token)
            account_info = api.get_account_info()

            if account_info and account_info.get("success") == 200:
                print_color("●▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬●", 0, 255, 255)
                print_color("  ✅ THÔNG TIN TÀI KHOẢN:", 0, 255, 255)
                print_color(f"  👤 User : {account_info.get('data').get('user')}", 200, 200, 200)
                print_color(f"  💰 Xu   : {account_info.get('data').get('xu')}", 255, 215, 0)
                print_color(f"  🔴 Xudie: {account_info.get('data').get('xudie')}", 255, 99, 71)
                print_color("●▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬●", 0, 255, 255)
                break # Token đúng, thoát vòng lặp để tiếp tục
            else:
                print_color("❌ Lỗi: Token không hợp lệ hoặc không thể lấy thông tin!", 255, 0, 0)
                # Xóa file token cũ để yêu cầu nhập lại
                if os.path.exists("tds_token.json"):
                    os.remove("tds_token.json")
                    print_color("♻️  Đã Reset Token, vui lòng nhập lại...", 255, 255, 0)
                print("-" * 30)
        setup_adb_connection(account_info.get('data').get('user'), account_info.get('data').get('xu'), account_info.get('data').get('xudie'))
        SERVICE_MAP = {
                # TikTok
                "TIKTOK_FOLLOW": {"service": "tiktok_follow", "cache_type": "TIKTOK_FOLLOW_CACHE", "claim_type": "TIKTOK_FOLLOW", "is_fixed_claim_id": True},
                "TIKTOK_LIKE": {"service": "tiktok_like", "cache_type": "TIKTOK_LIKE_CACHE", "claim_type": "TIKTOK_LIKE", "is_fixed_claim_id": True},
                # Facebook
                "FACEBOOK_FOLLOW": {"service": "facebook_follow", "cache_type": "facebook_follow_cache", "claim_type": "facebook_follow", "is_fixed_claim_id": True},
                "FACEBOOK_SHARE": {"service": "facebook_share", "cache_type": "facebook_share_cache", "claim_type": "facebook_share", "is_fixed_claim_id": True},
                "FACEBOOK_REACTION": {"service": "facebook_reaction", "cache_type": "facebook_reaction_cache", "claim_type": "facebook_reaction", "is_fixed_claim_id": True},
                "FACEBOOK_REACTIONCMT": {"service": "facebook_reactioncmt", "cache_type": "facebook_reactioncmt_cache", "claim_type": "facebook_reactioncmt", "is_fixed_claim_id": True},
                "FACEBOOK_PAGE": {"service": "facebook_page", "cache_type": "facebook_page_cache", "claim_type": "facebook_page", "is_fixed_claim_id": True},
                # Instagram
                "INSTAGRAM_FOLLOW": {"service": "instagram_follow", "cache_type": "INS_FOLLOW_CACHE", "claim_type": "INS_FOLLOW", "is_fixed_claim_id": True},
                "INSTAGRAM_LIKE": {"service": "instagram_like", "cache_type": None, "claim_type": "INS_LIKE", "is_fixed_claim_id": False},
                "INSTAGRAM_COMMENT": {"service": "instagram_comment", "cache_type": None, "claim_type": "INS_COMMENT", "is_fixed_claim_id": False},
            }
        print_color("--- 🛠️ CẤU HÌNH NHIỆM VỤ ---", 0, 255, 255)
        print("  [1] Nền tảng TikTok")
        print("  [2] Nền tảng Facebook(chưa làm)")
        print("  [3] Nền tảng Instagram")
        while True:
            switch = int(input("👉 Vui lòng chọn: "))
            # Di chuyển con trỏ lên 2 dòng và xóa sạch
            sys.stdout.write("\033[F\033[K\033[F\033[K\033[F\033[K\033[F\033[K\033[F\033[K")
            sys.stdout.flush()
            match switch:
                case 1:
                    if "username" in user_data:
                        print_color("📡 Đang quét danh sách tài khoản TikTok...", 0, 255, 255)
                        unique_ids = get_accounts_from_web(user_data['username'], user_data['password'], 'chtiktok')
                        
                        if unique_ids:
                            print_color("--- 📋 DANH SÁCH TÀI KHOẢN TIKTOK ---", 255, 255, 0)
                            for i, uid in enumerate(unique_ids):
                                print_color(f"  [{i + 1}] ID: {uid}", 200, 200, 200)
                            choice_idx = int(input(color_text("\n👉 Chọn số thứ tự tài khoản: ", 0, 255, 255)))
                            config_id = unique_ids[choice_idx - 1]
                            sys.stdout.write("\033[F\033[K" * (len(unique_ids) + 4))
                            sys.stdout.flush()
                        else:
                            print_color("⚠️ Không tìm thấy tài khoản TikTok nào đang bật.", 255, 255, 0)
                            config_id = input(color_text("🆔 Nhập Config ID (Tiktok) thủ công: ", 0, 255, 255))
                    else:
                        config_id = input(color_text("🆔 Nhập Config ID (Tiktok): ", 0, 255, 255))
                    
                    set_config = api.set_config_id("tiktok_run", config_id).get("data")
                    if set_config is not None:
                        config_id = set_config.get("id")
                        unique_id = set_config.get("uniqueID")
                        print_color(f"Tài khoản sử dụng: {unique_id}", 0, 255, 255)
                        print_color("✅ Cấu hình thành công!", 0, 255, 0)
                    else:
                        print_color("❌ Lỗi: Cấu hình không hợp lệ!", 255, 0, 0)
                    print_color("\n--- 📱 DỊCH VỤ TIKTOK ---", 255, 105, 180)
                    print("  [1] Follow\n  [2] Like\n  [3] Tích hợp")
                    while True:
                        match int(input("👉 Chọn dịch vụ: ")):
                            case 1: SERVICES_TO_RUN = ["TIKTOK_FOLLOW"]
                            case 2: SERVICES_TO_RUN = ["TIKTOK_LIKE"]
                            case 3: SERVICES_TO_RUN = ["TIKTOK_FOLLOW", "TIKTOK_LIKE"]
                            case _:
                                print_color("❌ Loại dịch vụ không hợp lệ!", 255, 0, 0)
                                continue
                        break
                    cooldown = int(input("⏳ Thời gian nghỉ giữa job (giây): "))
                    count_cache_to_claim = int(input("📦 Số lượng nhiệm vụ để gom nhận xu: "))
                    input(color_text("\n🚀 Tất cả đã sẵn sàng! Nhấn Enter để bắt đầu...", 0, 255, 255))
                    # Di chuyển con trỏ lên 2 dòng và xóa sạch
                    sys.stdout.write("\033[F\033[K\033[F\033[K")
                    sys.stdout.flush()
                    break
                case 2:
                    print_color("--- 👥 DỊCH VỤ FACEBOOK ---", 24, 119, 242)
                    if "username" in user_data:
                        print_color("\n📡 Đang quét danh sách tài khoản Facebook....")

                    break
                case 3:
                    if "username" in user_data:
                        print_color("\n📡 Đang quét danh sách tài khoản Instagram...", 0, 255, 255)
                        unique_ids = get_accounts_from_web(user_data['username'], user_data['password'], 'chinstagram')
                        
                        if unique_ids:
                            print_color("--- 📋 DANH SÁCH TÀI KHOẢN INSTAGRAM ---", 255, 255, 0)
                            for i, uid in enumerate(unique_ids):
                                print_color(f"  [{i + 1}] ID: {uid}", 200, 200, 200)
                            choice_idx = int(input(color_text("\n👉 Chọn số thứ tự tài khoản: ", 0, 255, 255)))
                            config_id = unique_ids[choice_idx - 1]
                            sys.stdout.write("\033[F\033[K" * (len(unique_ids) + 4))
                            sys.stdout.flush()
                        else:
                            print_color("⚠️ Không tìm thấy nick Instagram nào đang bật.", 255, 255, 0)
                            config_id = input("🆔 Nhập Config ID (Instagram) thủ công: ")
                    else:
                        config_id = input("🆔 Nhập Config ID (Instagram): ")
                    set_config = api.set_config_id("instagram_run", config_id).get("data")
                    if set_config is not None:
                        config_id = set_config.get("id")
                        unique_id = set_config.get("uniqueID")
                        print_color(f"Tài khoản sử dụng: {unique_id}", 0, 255, 255)
                        print_color("✅ Cấu hình thành công!", 0, 255, 0)
                    else:
                        print_color("❌ Lỗi: Cấu hình không hợp lệ!", 255, 0, 0)
                    print_color("--- 👥 DỊCH VỤ INSTAGRAM ---", 225, 48, 108)
                    print("  [1] Follow\n  [2] Like\n  [3] Comment\n  [4] Tích hợp")
                    while True:
                        match int(input("👉 Chọn dịch vụ: ")):
                            case 1: SERVICES_TO_RUN = ["INSTAGRAM_FOLLOW"]
                            case 2: SERVICES_TO_RUN = ["INSTAGRAM_LIKE"]
                            case 3: SERVICES_TO_RUN = ["INSTAGRAM_COMMENT"]
                            case 4: SERVICES_TO_RUN = ["INSTAGRAM_FOLLOW", "INSTAGRAM_LIKE", "INSTAGRAM_COMMENT"]
                            case _:
                                print_color("❌ Loại dịch vụ không hợp lệ!", 255, 0, 0)
                                continue
                        break
                    cooldown = int(input("⏳ Thời gian nghỉ giữa job (giây): "))
                    count_cache_to_claim = int(input("📦 Số lượng nhiệm vụ để gom nhận xu: "))
                    
                    input(color_text("\n🚀 Tất cả đã sẵn sàng! Nhấn Enter để bắt đầu...", 0, 255, 255))
                    sys.stdout.write("\033[F\033[K\033[F\033[K")
                    sys.stdout.flush()
                    break
                case _:
                    print_color("❌ Không có lựa chọn này!", 255, 0, 0)
                    continue

        service_idx = 0
        while True:
            current_service_name = SERVICES_TO_RUN[service_idx]
            status = run_auto_loop(api, SERVICE_MAP[current_service_name], cooldown, count_cache_to_claim)
            
            if status == "OUT_OF_JOBS":
                if len(SERVICES_TO_RUN) > 1:
                    service_idx = (service_idx + 1) % len(SERVICES_TO_RUN)
                    print_color(f"\n🔄 Hết nhiệm vụ {current_service_name}. Chuyển sang: {SERVICES_TO_RUN[service_idx]}", 0, 255, 255)
                    time.sleep(2)
                    continue
                else:
                    print_color(f"\n🔔 Hết nhiệm vụ {current_service_name}. Nghỉ 30s rồi kiểm tra lại...", 255, 255, 0)
                    time.sleep(30)
                    continue
            break # Dừng nếu thoát vòng lặp bình thường
    except KeyboardInterrupt:
        print("\n❌ Đã dừng chương trình, hẹn gặp lại!")
        exit(0)
    except Exception as e:
        print(f"❌ Lỗi: {e}")
        exit(1)