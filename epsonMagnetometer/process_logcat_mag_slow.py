import sys
import matplotlib.pyplot as plt
import matplotlib.animation as animation
import time

# The android device will return magnetometer and orientation data over logcat. The user will pipe this data in to this script as:
#   adb logcat | process_logcat_mag.py

# We expect data of the following comma-delimited format:
# Magnetometer:
#   magnetometer:<x axis>,<y axis>,<z axis>
# Orientation:
#   orientation:<yaw>,<pitch>,<roll>

#Every 100 data points the distributions of magnetometer and orientation data will be plotted in separate charts

num_hist_bins_or = 100
num_hist_bins_mag = 10
plot_update_interval = 1000

num_magnetometer_samples = 0
magnetometer_x = []
magnetometer_y = []
magnetometer_z = []

num_orientation_samples  = 0
orientation_x = []
orientation_y = []
orientation_z = []


#setting up subplots
fig = plt.figure()

mag_x_hist_ax = fig.add_subplot(4,3,1)
mag_y_hist_ax = fig.add_subplot(4,3,2)
mag_z_hist_ax = fig.add_subplot(4,3,3)

mag_x_scatter_ax = fig.add_subplot(4,3,4)
mag_y_scatter_ax = fig.add_subplot(4,3,5)
mag_z_scatter_ax = fig.add_subplot(4,3,6)

or_x_hist_ax = fig.add_subplot(4,3,7)
or_y_hist_ax = fig.add_subplot(4,3,8)
or_z_hist_ax = fig.add_subplot(4,3,9)

or_x_scatter_ax = fig.add_subplot(4,3,10)
or_y_scatter_ax = fig.add_subplot(4,3,11)
or_z_scatter_ax = fig.add_subplot(4,3,12)

mag_x_hist_ax.set_title("X-Axis Magnetometer Histogram")
mag_y_hist_ax.set_title("Y-Axis Magnetometer Histogram")
mag_z_hist_ax.set_title("Z-Axis Magnetometer Histogram")

mag_x_scatter_ax.set_title("X-Axis Magnetometer Scatterplot")
mag_y_scatter_ax.set_title("Y-Axis Magnetometer Scatterplot")
mag_z_scatter_ax.set_title("Z-Axis Magnetometer Scatterplot")

or_x_hist_ax.set_title("X-Axis Orientation Histogram")
or_y_hist_ax.set_title("Y-Axis Orientation Histogram")
or_z_hist_ax.set_title("Z-Axis Orientation Histogram")

or_x_scatter_ax.set_title("X-Axis Orientation Scatterplot")
or_y_scatter_ax.set_title("Y-Axis Orientation Scatterplot")
or_z_scatter_ax.set_title("Z-Axis Orientation Scatterplot")



f = open("log2.txt", "r")

def draw():
    print("draw called")
    num_magnetometer_samples = 0
    num_orientation_samples  = 0
    for line in f:
        # Updating data for magnetometer / orientation
        if "I/magnetometer" in line:
            num_magnetometer_samples += 1
            line_data = line.split(": ")[1].split("\n")[0].split(",")
            magnetometer_x.append(float(line_data[0]))
            magnetometer_y.append(float(line_data[1]))
            magnetometer_z.append(float(line_data[2]))

        if "I/orientation" in line:
            num_orientation_samples += 1
            line_data = line.split(": ")[1].split("\n")[0].split(",")
            orientation_x.append(float(line_data[0]))
            orientation_y.append(float(line_data[1]))
            orientation_z.append(float(line_data[2]))

    print("Captured " + str(num_magnetometer_samples) + " magnetometer samples")

    mag_x_hist_ax.hist(magnetometer_x, bins=num_hist_bins_mag)
    mag_y_hist_ax.hist(magnetometer_y, bins=num_hist_bins_mag)
    mag_z_hist_ax.hist(magnetometer_z, bins=num_hist_bins_mag)

    mag_x_scatter_ax.plot(magnetometer_x)
    mag_y_scatter_ax.plot(magnetometer_y)
    mag_z_scatter_ax.plot(magnetometer_z)

    print("Captured " + str(num_orientation_samples) + " orientation samples")

    or_x_hist_ax.hist(orientation_x, bins=num_hist_bins_or)
    or_y_hist_ax.hist(orientation_y, bins=num_hist_bins_or)
    or_z_hist_ax.hist(orientation_z, bins=num_hist_bins_or)

    or_x_scatter_ax.plot(orientation_x)
    or_y_scatter_ax.plot(orientation_y)
    or_z_scatter_ax.plot(orientation_z)

print("plotting")
draw()
plt.show()




