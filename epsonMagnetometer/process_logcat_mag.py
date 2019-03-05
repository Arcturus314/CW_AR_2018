import sys
import matplotlib.pyplot as plt

# The android device will return magnetometer and orientation data over logcat. The user will pipe this data in to this script as:
#   adb logcat | process_logcat_mag.py

# We expect data of the following comma-delimited format:
# Magnetometer:
#   magnetometer:<x axis>,<y axis>,<z axis>
# Orientation:
#   orientation:<yaw>,<pitch>,<roll>

#Every 100 data points the distributions of magnetometer and orientation data will be plotted in separate charts

num_hist_bins = 100
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
plt.ion()
fig = plt.figure()

mag_x_hist_ax = fig.add_subplot(4,3,1)
mag_y_hist_ax = fig.add_subplot(4,3,2)
mag_z_hist_ax = fig.add_subplot(4,3,3)

mag_x_scatter_ax = fig.add_subplot(4,3,4)
mag_y_scatter_ax = fig.add_subplot(4,3,5)
mag_z_scatter_ax = fig.add_subplot(5,3,6)

or_x_hist_ax = fig.add_subplot(4,3,7)
or_y_hist_ax = fig.add_subplot(4,3,8)
or_z_hist_ax = fig.add_subplot(4,3,9)

mag_x_scatter_ax = fig.add_subplot(4,3,10)
mag_y_scatter_ax = fig.add_subplot(4,3,11)
mag_z_scatter_ax = fig.add_subplot(4,3,12)

#initializing plots
mag_x_hist, = mag_x_hist_ax.hist(magnetometer_x)
mag_y_hist, = mag_y_hist_ax.hist(magnetometer_y)
mag_z_hist, = mag_z_hist_ax.hist(magnetometer_z)

mag_x_scatter, = mag_x_scatter_ax.plot(magnetometer_x)
mag_y_scatter, = mag_y_scatter_ax.plot(magnetometer_y)
mag_z_scatter, = mag_z_scatter_ax.plot(magnetometer_z)

or_x_hist, = or_x_hist_ax.hist(orientation_x)
or_y_hist, = or_y_hist_ax.hist(orientation_y)
or_z_hist, = or_z_hist_ax.hist(orientation_z)

or_x_scatter, = or_x_scatter.plot(orientation_x)
or_y_scatter, = or_y_scatter.plot(orientation_y)
or_z_scatter, = or_z_scatter.plot(orientation_z)

for line in sys.stdin:
    # Updating data for magnetometer / orientation
    print("read line " + line)
    if "magnetometer" in line:
        print("read magnetometer: " + line)
        num_magnetometer_samples += 1
        line_data = line.split(": ")[1].split(",")
        magnetometer_x.append(float(line_data[0]))
        magnetometer_y.append(float(line_data[1]))
        magnetometer_z.append(float(line_data[2]))

    if "orientation" in line:
        print("read orientation: " + line)
        num_orientation_samples += 1
        line_data = line.split(": ")[1].split(",")
        orientation_x.append(float(line_data[0]))
        orientation_y.append(float(line_data[1]))
        orientation_z.append(float(line_data[2]))

    # Dynamically replotting
    if num_magnetometer_samples % 1000 == 0:
        print("Captured " + str(num_magnetometer_samples) + " magnetometer samples")
        mag_x_hist.set_xdata(magnetometer_x)
        mag_y_hist.set_xdata(magnetometer_y)
        mag_z_hist.set_xdata(magnetometer_z)

        mag_x_scatter.set_xdata(magnetometer_x)
        mag_y_scatter.set_xdata(magnetometer_y)
        mag_z_scatter.set_xdata(magnetometer_z)

        plt.draw()

    if num_magnetometer_samples % 1000 == 0:
        print("Captured " + str(num_magnetometer_samples) + " magnetometer samples")
        or_x_hist.set_xdata(orientation_x)
        or_y_hist.set_xdata(orientation_y)
        or_z_hist.set_xdata(orientation_z)

        or_x_scatter.set_xdata(orientation_x)
        or_y_scatter.set_xdata(orientation_y)
        or_z_scatter.set_xdata(orientation_z)

        plt.draw()



