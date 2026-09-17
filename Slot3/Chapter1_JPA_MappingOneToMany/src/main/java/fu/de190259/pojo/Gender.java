package fu.de190259.pojo;

/**
 * Enum đại diện giới tính nhân viên.
 * Dùng @Enumerated(EnumType.STRING) ở Employee để lưu chuỗi MALE/FEMALE/OTHER,
 * tránh vỡ dữ liệu nếu thay đổi thứ tự hằng số.
 */
public enum Gender {
    MALE,
    FEMALE,
    OTHER
}
